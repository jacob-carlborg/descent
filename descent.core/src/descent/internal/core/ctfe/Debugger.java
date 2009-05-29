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
import descent.core.compiler.CharOperation;
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
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.core.CompilationUnit;

public class Debugger implements IDebugger {

	final CompilationUnit fUnit;
	final int fOffset;
	int fLine;
	final IOutput fOutput;	
	final Map<ICompilationUnit, Document> fCompilationUnitDocuments;
	Thread fThread;
	ResourceSearch fSearch;
	ExpressionEvaluator fEvaluator;
	VariablesFinder fVariablesFinder;
	
	List<Breakpoint> breakpoints = new ArrayList<Breakpoint>();
	IDebuggerListener fListener;
	IDebugElementFactory fElementFactory;
	int fStartedDebugging;
	Semaphore fSemaphore;
	
	ParseResult fParseResult;
	int fCurrentLine;
	int fCurrentStackFrame;
	ICompilationUnit fCurrentUnit;
	Scope fCurrentScope;
	InterState fCurrentInterState;
	
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
		this.fSearch = new ResourceSearch();
		this.fSemaphore = new Semaphore(0);
		this.fCompilationUnitDocuments = new HashMap<ICompilationUnit, Document>();
		this.fLine = getLine(fUnit, fOffset);
	}
	
	public void initialize(IDebuggerListener listener, IDebugElementFactory elementFactory) {
		this.fListener = listener;
		this.fElementFactory = elementFactory;
	}

	public void addBreakpoint(IResource resource, int lineNumber) {
		ICompilationUnit unit = (ICompilationUnit) JavaCore.create(resource);
		if (unit != null) {
			breakpoints.add(new Breakpoint(unit, lineNumber));
		}
	}

	public void removeBreakpoint(IResource resource, int lineNumber) {
		ICompilationUnit unit = (ICompilationUnit) JavaCore.create(resource);
		if (unit != null) {
			breakpoints.remove(new Breakpoint(unit, lineNumber));
		}
	}

	public void start() {
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					fParseResult = CompilationUnitResolver.prepareForResolve(AST.D1, 
							fUnit, fUnit.getJavaProject(), JavaCore.getOptions(), 
							null, false, false, Debugger.this, new NullProgressMonitor());
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

	public void terminate() {
		fSemaphore.release();
	}

	public void stepBegin(ASTDmdNode node, Scope sc) {
		fCurrentInterState = null;
		fCurrentScope = sc;
		
		internalStepBegin(node);
	}
	
	public void stepEnd(ASTDmdNode node, Scope sc) {
		fCurrentInterState = null;
		fCurrentScope = sc;
		
		internalStepEnd(node);
	}
	
	public void stepBegin(ASTDmdNode node, InterState is) {
		fCurrentInterState = is;
		fCurrentScope = is == null ? ((FuncDeclaration) node).scope : is.fd.scope;
		
		internalStepBegin(node);
	}
	
	public void stepEnd(ASTDmdNode node, InterState is) {
		fCurrentInterState = is;
		fCurrentScope = is == null ? ((FuncDeclaration) node).scope : is.fd.scope;
		
		internalStepEnd(node);
	}
	
	private void internalStepBegin(ASTDmdNode node) {
		try {
			internalStepBegin0(node);
		} catch (InterruptedException e) {
			bug(e);
		}
	}
	
	private void internalStepBegin0(ASTDmdNode node) throws InterruptedException {
		ICompilationUnit unit = getCompilationUnit(node, fCurrentScope);
		if (unit == null) return;
		
		fCurrentUnit = unit;
		
		int line = -1;
		boolean justStartedDebugging = false;
		
		if (fStartedDebugging == 0) {
			line = getLine(unit, node);
			
			if (isTarget(unit, line)) {
				justStartedDebugging = true;
				fStartedDebugging++;
			} else {
				return;
			}
		}
		
		if (fStartedDebugging <= 0)
			return;
		
		if (line == -1)
			line = getLine(unit, node);
		
		if (line == fCurrentLine && fStackFrames.size() == fCurrentStackFrame)
			return;
		
		fCurrentLine = line;
		
		if (fStackFrames.isEmpty()) {
			enterStackFrame();
			fNextStackFrame = 0;
		}
		
		fCurrentStackFrame = fStackFrames.size();
		
		// Only hit breakpoint if the expected stack frame for the user
		// is the same as the current one
		if (fNextStackFrame >= fCurrentStackFrame - 1) {
			
			// This is if we didn't just started debugging and the node
			// is one of the ones that can be debugged, don't suspend the exeuction
			if ((node instanceof CallExp || node instanceof TemplateInstance) && !justStartedDebugging)
				return;
			
			if (justStartedDebugging) {
				fListener.breakpointHit(unit, line);
			} else {
				fListener.stepEnded();
			}
			fSemaphore.acquire();
		} else {
			// See if we hit a breakpoint
			if (hasBreakpoint(unit, fCurrentLine)) {
				fListener.breakpointHit(unit, line);
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
		ICompilationUnit unit = getCompilationUnit(node, fCurrentScope);
		if (unit == null) return;
		
		fCurrentUnit = unit;
		
		if (fStartedDebugging <= 0)
			return;
		
		int line = -1;
		
		if (fNextStackFrameChanged) {
			line = getLine(unit, node);
				
			if (line == fCurrentLine && fStackFrames.size() == fCurrentStackFrame)
				return;
		
			fCurrentLine = line;
			fCurrentStackFrame = fStackFrames.size();
			
			if (fNextStackFrame == fCurrentStackFrame - 1) {
				fListener.stepEnded();
				fSemaphore.acquire();
			}
		}
		
		if (line == -1)
			line = getLine(unit, node);
		
		if (isTarget(unit, line)) {
			fStartedDebugging--;
		}
	}
	
	private ICompilationUnit getCompilationUnit(ASTDmdNode node, Scope sc) {
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
		
		Module module = sc.module;
		ICompilationUnit unit = module.getJavaElement();
		if (unit == null) {
			unit = fSearch.getNameEnvironment().findCompilationUnit(CharOperation.splitOn('.', module.moduleName.toCharArray()));
		}
		return unit;
	}
	
	private boolean isTarget(ICompilationUnit unit, int line) {
		return unit.equals(fUnit) && line == fLine;
	}

	private boolean hasBreakpoint(ICompilationUnit unit, int line) {
		for(Breakpoint b : breakpoints) {
			if (b.unit.equals(unit) && b.line == line)
				return true;
		}
		return false;
	}

	public void stepInto() {
		int newStackFrame = fStackFrames.size();
		fNextStackFrameChanged = newStackFrame != fNextStackFrame;
		fNextStackFrame = newStackFrame;
		
		fListener.resumed(DebugEvent.STEP_INTO);
		fSemaphore.release();
	}

	public void stepOver() {
		fNextStackFrameChanged = false;
		fNextStackFrame = fStackFrames.size() - 1;
		
		fListener.resumed(DebugEvent.STEP_OVER);
		fSemaphore.release();
	}

	public void stepReturn() {
		int newStackFrame = fStackFrames.size() - 2;
		fNextStackFrameChanged = newStackFrame != fNextStackFrame;
		fNextStackFrame = newStackFrame;
		
		fListener.resumed(DebugEvent.STEP_RETURN);
		fSemaphore.release();
	}
	
	public void resume() {
		int newStackFrame = -1;
		fNextStackFrameChanged = newStackFrame != fNextStackFrame;
		fNextStackFrame = newStackFrame;
		
		fListener.resumed(DebugEvent.RESUME);
		fSemaphore.release();
	}

	public synchronized IVariable evaluateExpression(int stackFrame, String expression) {
		IDescentStackFrame sf = getStackFrame(stackFrame);
		return fEvaluator.evaluate(sf, expression);
	}

	public IVariable[] getVariables(int stackFrame) {
		IDescentStackFrame sf = getStackFrame(stackFrame);
		return fVariablesFinder.getVariables(sf);
	}

	public IStackFrame[] getStackFrames() {
		if (!fStackFrames.isEmpty()) {
			fStackFrames.remove(0);
			fStackFrames.add(0, newStackFrame());
		}
		
		return fStackFrames.toArray(new IStackFrame[fStackFrames.size()]);
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
	
	public void enterStackFrame() {
		fStackFrames.add(0, newStackFrame());
	}
	
	public void exitStackFrame() {
		fStackFrames.remove(0);
	}

	public void message(String message) {
		fOutput.message(message);
	}
	
	private int getLine(ICompilationUnit unit, ASTDmdNode node) {
		return getLine(unit, node.start);
	}
	
	private int getLine(ICompilationUnit unit, int start) {
		Document doc = getDocument(unit);
		try {
			return doc.getLineOfOffset(start) + 1;
		} catch (BadLocationException e) {
			bug(e);
			return 0;
		}
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
