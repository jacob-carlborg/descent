package descent.internal.core.ctfe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.ctfe.IDebugElementFactory;
import descent.core.ctfe.IDebugger;
import descent.core.ctfe.IDebuggerListener;
import descent.core.ctfe.IDescentStackFrame;
import descent.core.ctfe.IOutput;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnitResolver;
import descent.core.dom.CompilationUnitResolver.ParseResult;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.AstVisitorAdapter;
import descent.internal.core.CompilationUnit;

public class Debugger implements IDebugger {

	/**
	 * The unit where the symbol to debug is located.
	 */
	final CompilationUnit fUnit;
	
	/**
	 * The offset of the symbol to debug. 
	 */
	final int fOffset;
	
	/**
	 * The node of the symbol to debug.
	 */
	ASTNode fTarget;
	
	/**
	 * To send messages to the console for pragmas, or for errors.
	 */
	final IOutput fOutput;
	
	/**
	 * Instead of augmenting the parser funcionality to make node retain
	 * lines, we create a document for each compilation and from that we
	 * deduce the line given an offset. We maintain a cache of
	 * units to documents.
	 */
	final Map<ICompilationUnit, Document> fCompilationUnitDocuments;
	
	/**
	 * The thread that executes the semantic analysis.
	 */
	Thread fThread;
	
	/**
	 * Delegate to evaluate expressions.
	 */
	ExpressionEvaluator fEvaluator;
	
	/**
	 * Delegate to find variables in the current stack frame.
	 */
	VariablesFinder fVariablesFinder;
	
	/**
	 * The breakpoints created by the user.
	 */
	List<Breakpoint> breakpoints = new ArrayList<Breakpoint>();
	
	/**
	 * To notify events about the debug session.
	 */
	IDebuggerListener fListener;
	
	/**
	 * To create stack frames and variables.
	 */
	IDebugElementFactory fElementFactory;
	
	/**
	 * Did the semantic analysis already passed through the symbol
	 * to debug? If yes, this value is > 0.
	 */
	int fStartedDebugging;
	
	/**
	 * Holds execution of the semantic analysis when a step finishes, or
	 * when a breakpoint is it. 
	 */
	Semaphore fSemaphore;
	
	/**
	 * Contains the parse result of the unit to debug, plus its semantic context.
	 */
	ParseResult fParseResult;
	
	/**
	 * The current line and stack frame in the execution,
	 * so that we don't suspend the debugger twice for the same line
	 * and stack frame.
	 */
	int fCurrentLine;
	int fCurrentStackFrame;
	
	/**
	 * The unit being analyzed by the semantic analysis in the current
	 * debug session.
	 */
	ICompilationUnit fCurrentUnit;
	
	/**
	 * Current scope and interpretation states.
	 */
	Scope fCurrentScope;
	InterState fCurrentInterState;
	
	/**
	 * The stack frames of the debug session.
	 */
	List<IDescentStackFrame> fStackFrames = new ArrayList<IDescentStackFrame>();
	
	/**
	 * This is the next stack frame where the user wants to be.
	 * If it says: step over, it reamins the same.
	 * If it says: step into, it is one + current number of stack frames.
	 * If it says: step out, it it one - current number of stack frames.
	 * If it says: continue, it is -1.
	 */
	int fNextStackFrame = -1;
	boolean fNextStackFrameChanged = true;

	public Debugger(CompilationUnit unit, int offset, IOutput output) {
		this.fUnit = unit;
		this.fOffset = offset;
		this.fOutput = output;
		this.fSemaphore = new Semaphore(0);
		this.fCompilationUnitDocuments = new HashMap<ICompilationUnit, Document>();
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#initialize(descent.core.ctfe.IDebuggerListener, descent.core.ctfe.IDebugElementFactory)
	 */
	public void initialize(IDebuggerListener listener, IDebugElementFactory elementFactory) {
		this.fListener = listener;
		this.fElementFactory = elementFactory;
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#addBreakpoint(org.eclipse.core.resources.IResource, int)
	 */
	public void addBreakpoint(IResource resource, int lineNumber) {
		ICompilationUnit unit = (ICompilationUnit) JavaCore.create(resource);
		if (unit != null) {
			breakpoints.add(new Breakpoint(unit, lineNumber));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#removeBreakpoint(org.eclipse.core.resources.IResource, int)
	 */
	public void removeBreakpoint(IResource resource, int lineNumber) {
		ICompilationUnit unit = (ICompilationUnit) JavaCore.create(resource);
		if (unit != null) {
			breakpoints.remove(new Breakpoint(unit, lineNumber));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#start()
	 */
	public void start() {
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					fParseResult = CompilationUnitResolver.prepareForResolve(AST.D1, 
							fUnit, fUnit.getJavaProject(), JavaCore.getOptions(), 
							null, false, false, Debugger.this, new NullProgressMonitor());
					fTarget = getTarget(fParseResult.module, fOffset);
					
					fParseResult.context.problemRequestor = new ProblemRequestor(Debugger.this);
					fEvaluator = new ExpressionEvaluator(fUnit, fParseResult, fElementFactory);
					fVariablesFinder = new VariablesFinder(fElementFactory);
					CompilationUnitResolver.resolve(fParseResult, fUnit.getJavaProject(), null, Debugger.this);
				} catch (JavaModelException e) {
					bug(e);
				}
				
				fListener.terminated();
			}
		};
		
		fThread = new Thread(runnable);
		fThread.start();
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#terminate()
	 */
	public void terminate() {
		fSemaphore.release();
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#stepBegin(descent.internal.compiler.parser.ASTDmdNode, descent.internal.compiler.parser.Scope)
	 */
	public void stepBegin(ASTDmdNode node, Scope sc) {
		fCurrentInterState = null;
		fCurrentScope = sc;
		
		internalStepBegin(node);
	}
	
	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#stepEnd(descent.internal.compiler.parser.ASTDmdNode, descent.internal.compiler.parser.Scope)
	 */
	public void stepEnd(ASTDmdNode node, Scope sc) {
		fCurrentInterState = null;
		fCurrentScope = sc;
		
		internalStepEnd(node);
	}
	
	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#stepBegin(descent.internal.compiler.parser.ASTDmdNode, descent.internal.compiler.parser.InterState)
	 */
	public void stepBegin(ASTDmdNode node, InterState is) {
		fCurrentInterState = is;
		fCurrentScope = is == null ? ((FuncDeclaration) node).scope : is.fd.scope;
		
		internalStepBegin(node);
	}
	
	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#stepEnd(descent.internal.compiler.parser.ASTDmdNode, descent.internal.compiler.parser.InterState)
	 */
	public void stepEnd(ASTDmdNode node, InterState is) {
		fCurrentInterState = is;
		fCurrentScope = is == null ? ((FuncDeclaration) node).scope : is.fd.scope;
		
		internalStepEnd(node);
	}
	
	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#stepInto()
	 */
	public void stepInto() {
		stepInto(fStackFrames.size() - 1);
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#stepInto()
	 */
	public void stepInto(int stackFrame) {
		int newStackFrame = stackFrame + 1;
		fNextStackFrameChanged = newStackFrame != fNextStackFrame;
		fNextStackFrame = newStackFrame;
		
		fListener.resumed(DebugEvent.STEP_INTO);
		fSemaphore.release();
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#stepOver()
	 */
	public void stepOver() {
		stepOver(fStackFrames.size() - 1);
	}
	
	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#stepOver(int)
	 */
	public void stepOver(int stackFrame) {
		fNextStackFrameChanged = false;
		fNextStackFrame = stackFrame;
		
		fListener.resumed(DebugEvent.STEP_OVER);
		fSemaphore.release();
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#stepReturn()
	 */
	public void stepReturn() {
		stepReturn(fStackFrames.size() - 1);
	}
	
	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#stepReturn(int)
	 */
	public void stepReturn(int stackFrame) {
		int newStackFrame = stackFrame - 1;
		fNextStackFrameChanged = newStackFrame != fNextStackFrame;
		fNextStackFrame = newStackFrame;
		
		fListener.resumed(DebugEvent.STEP_RETURN);
		fSemaphore.release();
	}
	
	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#resume()
	 */
	public void resume() {
		int newStackFrame = -1;
		fNextStackFrameChanged = newStackFrame != fNextStackFrame;
		fNextStackFrame = newStackFrame;
		
		fListener.resumed(DebugEvent.RESUME);
		fSemaphore.release();
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#evaluateExpression(int, java.lang.String)
	 */
	public IVariable evaluateExpression(int stackFrame, String expression) {
		IDescentStackFrame sf = getStackFrame(stackFrame);
		return fEvaluator.evaluate(sf, expression);
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#getVariables(int)
	 */
	public IVariable[] getVariables(int stackFrame) {
		IDescentStackFrame sf = getStackFrame(stackFrame);
		return fVariablesFinder.getVariables(sf);
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#getStackFrames()
	 */
	public IStackFrame[] getStackFrames() {
		if (!fStackFrames.isEmpty()) {
			fStackFrames.remove(0);
			fStackFrames.add(0, newStackFrame());
		}
		
		return fStackFrames.toArray(new IStackFrame[fStackFrames.size()]);
	}
	
	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#enterStackFrame(descent.internal.compiler.parser.ASTDmdNode)
	 */
	public void enterStackFrame(ASTDmdNode node) {
		if (node != null) {
			fCurrentUnit = getCompilationUnit(node);
		}
		
		fStackFrames.add(0, newStackFrame());
	}
	
	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#exitStackFrame()
	 */
	public void exitStackFrame(ASTDmdNode node) {
		fStackFrames.remove(0);
		if (fNextStackFrame > fStackFrames.size())
			fNextStackFrame = fStackFrames.size();
		
		if (!fStackFrames.isEmpty())
			fCurrentUnit = fStackFrames.get(0).getCompilationUnit();
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.ctfe.IDebugger#message(java.lang.String)
	 */
	public void message(String message) {
		fOutput.message(message);
	}
	
	private IDescentStackFrame getStackFrame(int stackFrame) {
		int index = fStackFrames.size() - stackFrame - 1;
		if (index < 0 || index >= fStackFrames.size())
			return null;
		
		return fStackFrames.get(index);
	}
	
	private IDescentStackFrame newStackFrame() {
		return fElementFactory.newStackFrame(fCurrentUnit.getFullyQualifiedName(), fStackFrames.size(), fCurrentUnit, fCurrentLine, fCurrentScope, fCurrentInterState);
	}
	
	private void internalStepBegin(ASTDmdNode node) {
		try {
			internalStepBegin0(node);
		} catch (InterruptedException e) {
			bug(e);
		}
	}
	
	private void internalStepBegin0(ASTDmdNode node) throws InterruptedException {
		boolean justStartedDebugging = false;
		
		if (fStartedDebugging == 0) {
			fCurrentUnit = getCompilationUnit(node);
			if (node == fTarget) {
				justStartedDebugging = true;
				fStartedDebugging++;
			} else {
				return;
			}
		}
		
		if (fStartedDebugging <= 0)
			return;
		
		int line = getLine(fCurrentUnit, node.start);
		if (line == fCurrentLine && fStackFrames.size() - 1 == fCurrentStackFrame)
			return;
		
		fCurrentLine = line;
		
		if (fStackFrames.isEmpty()) {
			enterStackFrame(null);
			fNextStackFrame = 0;
		}
		
		fCurrentStackFrame = fStackFrames.size() - 1;
		
		// Only hit breakpoint if the expected stack frame for the user
		// is the same as the current one
		if (fNextStackFrame >= fCurrentStackFrame) {
			
			// This is if we didn't just started debugging and the node
			// is one of the ones that can be debugged, don't suspend the exeuction
			if ((node instanceof CallExp || node instanceof TemplateInstance) && !justStartedDebugging)
				return;
			
			if (justStartedDebugging) {
				fListener.breakpointHit(fCurrentUnit, line);
			} else {
				fListener.stepEnded();
			}
			fSemaphore.acquire();
		} else {
			// See if we hit a breakpoint
			if (hasBreakpoint(fCurrentUnit, fCurrentLine)) {
				fListener.breakpointHit(fCurrentUnit, line);
				fSemaphore.acquire();
			}
		}
	}
	
	private void internalStepEnd(ASTDmdNode node) {
		try {
			internalStepEnd0(node);
		} catch (InterruptedException e) {
			bug(e);
		}
	}
	
	private void internalStepEnd0(ASTDmdNode node) throws InterruptedException {
		if (fStartedDebugging <= 0)
			return;
		
		int line = getLine(fCurrentUnit, node.start + node.length);
		if (line == -1) return;
		
		if (line == fCurrentLine && fStackFrames.size() - 1 == fCurrentStackFrame)
			return;
	
		fCurrentLine = line;
		fCurrentStackFrame = fStackFrames.size() - 1;
		
		if (!(node instanceof ConditionalDeclaration) && 
			!(node instanceof FuncDeclaration) &&
				fCurrentStackFrame <= fNextStackFrame) {
			fListener.stepEnded();
			fSemaphore.acquire();
		}
		
		if (node == fTarget) {
			fStartedDebugging--;
			if (fStartedDebugging == 0) {
				exitStackFrame(null);
			}
		}
	}
	
	private ICompilationUnit getCompilationUnit(ASTDmdNode node) {
		if (node instanceof Dsymbol) {
			Dsymbol sym = (Dsymbol) node;
			IJavaElement elem = sym.getJavaElement();
			if (elem != null) {
				ICompilationUnit unit = (ICompilationUnit) elem.getAncestor(IJavaElement.COMPILATION_UNIT);
				if (unit == null) {
					unit = (ICompilationUnit) elem.getAncestor(IJavaElement.CLASS_FILE);
				}
				return unit;
			}
		}
		return fUnit;
	}

	private boolean hasBreakpoint(ICompilationUnit unit, int line) {
		for(Breakpoint b : breakpoints) {
			if (b.unit.equals(unit) && b.line == line)
				return true;
		}
		return false;
	}
	
	private int getLine(ICompilationUnit unit, int offset) {
		Document doc = getDocument(unit);
		try {
			return doc.getLineOfOffset(offset) + 1;
		} catch (BadLocationException e) {
			bug(e);
			return 0;
		}
	}
	
	private ASTNode getTarget(Module module, final int offset) {
		final ASTNode[] target = { null };
		module.accept(new AstVisitorAdapter() {
			@Override
			public boolean visit(CallExp node) {
				return analyze(node);
			}
			@Override
			public boolean visit(TemplateInstance node) {
				return analyze(node);
			}
			@Override
			public boolean visit(TemplateMixin node) {
				return analyze(node);
			}
			private boolean analyze(ASTNode node) {
				if (node.start <= offset && offset <= node.start + node.length) {
					target[0] = node;
				}
				return true;
			}
		});
		return target[0];
	}
	
	Document getDocument(ICompilationUnit unit) {
		Document document = fCompilationUnitDocuments.get(unit);
		if (document == null) {
			try {
				document = new Document(unit.getSource());
			} catch (JavaModelException e) {
				bug(e);
			}
			fCompilationUnitDocuments.put(unit, document);
		}
		return document;
	}
	
	void bug(Exception e) {
		fOutput.error("Descent Bug, please report it ;-): " + e.getMessage());
		for(StackTraceElement elem : e.getStackTrace()) {
			fOutput.error(elem.toString());
		}
	}

}
