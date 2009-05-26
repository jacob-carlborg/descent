package descent.internal.core.ctfe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

import descent.core.ICompilationUnit;
import descent.core.IProblemRequestor;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnitResolver;
import descent.core.dom.CompilationUnitResolver.ParseResult;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.DsymbolTable;
import descent.internal.compiler.parser.ExpInitializer;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.ScopeDsymbol;
import descent.internal.compiler.parser.StringExp;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.core.CompilationUnit;
import descent.internal.core.ctfe.dom.CompileTimeSemanticContext;

public class CtfeDebugger implements ICtfeDebugger {

	private final CompilationUnit fUnit;
	private final int fOffset;
	private int fLine;
	private final ICtfeOutput fOutput;	
	private final Map<ICompilationUnit, Document> fCompilationUnitDocuments;
	private Thread fThread;
	private ResourceSearch fSearch;
	
	private List<Breakpoint> breakpoints = new ArrayList<Breakpoint>();
	private DescentCtfeDebugTarget fDebugTarget;
	private int fStartedDebugging;
	private Semaphore fSemaphore;
	
	private ParseResult fParseResult;
	private int fCurrentLine;
	private int fCurrentStackFrame;
	private ICompilationUnit fCurrentUnit;
	private Scope fCurrentScope;
	private InterState fCurrentInterState;
	
	private List<DescentCtfeStackFrame> fStackFrames = new ArrayList<DescentCtfeStackFrame>();
	
	/**
	 * This is the next stack frame where the user wants to be.
	 * If it says: step over, it reamins the same.
	 * If it says: step into, it is one + current number of stack frames.
	 * If it says: step out, it it one - current number of stack frames.
	 * If it says: continue, it is -1.
	 */
	private int fNextStackFrame = -1;
	private boolean fNextStackFrameChanged = true;
	
	private static class Breakpoint {
		
		ICompilationUnit unit;
		int line;
		
		public Breakpoint(ICompilationUnit unit, int line) {
			this.unit = unit;
			this.line = line;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + line;
			result = prime * result + ((unit == null) ? 0 : unit.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Breakpoint other = (Breakpoint) obj;
			if (line != other.line)
				return false;
			if (unit == null) {
				if (other.unit != null)
					return false;
			} else if (!unit.equals(other.unit))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return unit.getElementName() + ":" + line;
		}
		
	}

	public CtfeDebugger(CompilationUnit unit, int offset, ICtfeOutput output) {
		this.fUnit = unit;
		this.fOffset = offset;
		this.fOutput = output;
		this.fSearch = new ResourceSearch();
		this.fSemaphore = new Semaphore(0);
		this.fCompilationUnitDocuments = new HashMap<ICompilationUnit, Document>();
		
		Document doc = getDocument(unit);
		try {
			fLine = doc.getLineOfOffset(fOffset) + 1;
		} catch (BadLocationException e) {
			bug(e);
			fLine = 0;
		}
	}
	
	public void setDebugTarget(DescentCtfeDebugTarget debugTarget) {
		fDebugTarget = debugTarget;
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
							fUnit,
							fUnit.getJavaProject(), 
							JavaCore.getOptions(), 
							null, 
							false, 
							false, 
							CtfeDebugger.this,
							new NullProgressMonitor());
					fParseResult.context.problemRequestor = new IProblemRequestor() {
						public void acceptProblem(IProblem problem) {
							if (fCurrentUnit == null) {
								fOutput.error(problem.toString());
								return;
							}
							
							try {
								IDocument doc = getDocument(fCurrentUnit);
								int line = doc.getLineOfOffset(problem.getSourceStart()) + 1;
								
								fOutput.error(fCurrentUnit.getModuleName() + "(" + line + "): " + problem.toString());
								
								int oldLine = fCurrentLine;
								fCurrentLine = line;
								enterStackFrame();
								
								try {
									fDebugTarget.stepEnded();
									fSemaphore.acquire();
								} finally {
									exitStackFrame();
									fCurrentLine = oldLine;
								}
							} catch (Exception e) {
								bug(e);
								return;
							}
						}
						public void beginReporting() {
						}
						public void endReporting() {
						}
						public boolean isActive() {
							return true;
						}
					};
					
					CompilationUnitResolver.resolve(fParseResult, fUnit.getJavaProject(), null, CtfeDebugger.this);
				} catch (JavaModelException e) {
					bug(e);
				}
				
				try {
					fDebugTarget.terminate();
				} catch (DebugException e) {
					bug(e);
				}
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
		ICompilationUnit unit = getCompilationUnit(fCurrentScope);
		if (unit == null) return;
		
		fCurrentUnit = unit;
		
		IDocument doc = null;
		int line = -1;
		
		boolean justStartedDebugging = false;
		
		if (fStartedDebugging == 0) {
			try {
				doc = getDocument(unit);
				line = doc.getLineOfOffset(node.start) + 1;
			} catch (Exception e) {
				bug(e);
			}
			
			if (isTarget(unit, line)) {
				justStartedDebugging = true;
				fStartedDebugging++;
			} else {
				return;
			}
		}
		
		if (fStartedDebugging <= 0)
			return;
		
		try {
			if (doc == null) {
				doc = getDocument(unit);
				line = doc.getLineOfOffset(node.start) + 1;
			}
			
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
				if ((node instanceof CallExp || node instanceof TemplateInstance) && !justStartedDebugging)
					return;
				
				fDebugTarget.stepEnded();
				fSemaphore.acquire();
			} else {
				// See if we hit a breakpoint
				if (hasBreakpoint(unit, fCurrentLine)) {
					fDebugTarget.breakpointHit(unit.getResource(), line);
					fSemaphore.acquire();
				}
			}
		} catch (Exception e) {
			bug(e);
		}
	}
	
	private void internalStepEnd(ASTDmdNode node) {
		ICompilationUnit unit = getCompilationUnit(fCurrentScope);
		if (unit == null) return;
		
		fCurrentUnit = unit;
		
		IDocument doc = null;
		int line = -1;
		
		if (fStartedDebugging <= 0 || !fNextStackFrameChanged)
			return;
		
		try {
			doc = getDocument(unit);
			line = doc.getLineOfOffset(node.start) + 1;
			
			if (line == fCurrentLine && fStackFrames.size() == fCurrentStackFrame)
				return;
		
			fCurrentLine = line;
			fCurrentStackFrame = fStackFrames.size();
			
			if (fNextStackFrame == fCurrentStackFrame - 1) {
				fDebugTarget.stepEnded();
				fSemaphore.acquire();
			}
		} catch (Exception e) {
			bug(e);
		}
		
		if (doc == null) {
			try {
				doc = getDocument(unit);
				line = doc.getLineOfOffset(node.start) + 1;
			} catch (Exception e) {
				bug(e);
				return;
			}
		}
		
		if (isTarget(unit, line)) {
			fStartedDebugging--;
		}
	}
	
	private ICompilationUnit getCompilationUnit(Scope sc) {
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
		
		fSemaphore.release();
		fDebugTarget.resumed(DebugEvent.STEP_INTO);
	}

	public void stepOver() {
		fNextStackFrameChanged = false;
		fNextStackFrame = fStackFrames.size() - 1;
		
		fSemaphore.release();
		fDebugTarget.resumed(DebugEvent.STEP_OVER);
	}

	public void stepReturn() {
		int newStackFrame = fStackFrames.size() - 2;
		fNextStackFrameChanged = newStackFrame != fNextStackFrame;
		fNextStackFrame = newStackFrame;
		
		fSemaphore.release();
		fDebugTarget.resumed(DebugEvent.STEP_RETURN);
	}
	
	public void resume() {
		int newStackFrame = -1;
		fNextStackFrameChanged = newStackFrame != fNextStackFrame;
		fNextStackFrame = newStackFrame;
		
		fSemaphore.release();
		fDebugTarget.resumed(DebugEvent.RESUME);
	}

	public synchronized IVariable evaluateExpression(int stackFrame, String expression) {
		DescentCtfeStackFrame sf = getStackFrame(stackFrame);
		Scope scope = sf.getScope();
		InterState is = sf.getInterState();
		
		Parser parser = new Parser(AST.D1, expression.toCharArray(), 0, expression.length(), null, null, false, false, fUnit.getFullyQualifiedName().toCharArray(), fParseResult.encoder);
		parser.nextToken();
		
		Expression exp = parser.parseExpression();
		if (parser.problems != null && !parser.problems.isEmpty()) {
			return newVariable(stackFrame, expression, parser.problems.toString());	
		} else {
			((CompileTimeSemanticContext) fParseResult.context).disableStepping();
			IProblemRequestor oldRequestor = fParseResult.context.problemRequestor;
			int oldMuteProblems = fParseResult.context.muteProblems;
			int oldGlobalErrors = fParseResult.context.global.errors; 
			
			fParseResult.context.muteProblems = 0;
			fParseResult.context.global.errors = 0;
			
			final List<IProblem> problems = new ArrayList<IProblem>();
			fParseResult.context.problemRequestor = new IProblemRequestor() {
				public void acceptProblem(IProblem problem) {
					problems.add(problem);
				}
				public void beginReporting() {
				}
				public void endReporting() {
				}
				public boolean isActive() {
					return true;
				}
			};
			
			try {
				Expression result;
				
				if (is == null) {
					scope.flags |= Scope.SCOPEstaticif;
					
					result = exp.semantic(scope, fParseResult.context);
					result = result.optimize(ASTDmdNode.WANTflags | ASTDmdNode.WANTvalue | ASTDmdNode.WANTinterpret, fParseResult.context);
				} else {
					// Need a synthetic scope with the function's local symbol table
					// and the variables being interpreted
					Scope sc = Scope.copy(scope);
					sc.scopesym = new ScopeDsymbol();
					sc.scopesym.symtab = new DsymbolTable();					
					
					for(char[] key : is.fd.localsymtab.keys()) {
						if (key == null)
							continue;
						sc.scopesym.symtab.insert(key, is.fd.localsymtab.lookup(key));
					}
					
					for(Dsymbol sym : is.vars) {
						sc.scopesym.symtab.insert(sym);
					}
					
					sc.flags |= Scope.SCOPEstaticif;
					
					result = exp.semantic(sc, fParseResult.context);
					result = result.interpret(is, fParseResult.context);
				}
				
				if (problems.size() > 0) {
					return newVariable(stackFrame, expression, problems.toString());
				} else {
					return newVariable(stackFrame, expression, result);
				}
			} finally {
				fParseResult.context.problemRequestor = oldRequestor;
				fParseResult.context.muteProblems = oldMuteProblems;
				fParseResult.context.global.errors = oldGlobalErrors;
				((CompileTimeSemanticContext) fParseResult.context).enableStepping();
			}
		}
	}

	public IVariable[] getVariables(int stackFrame) {
		List<DescentCtfeVariable> vars = new ArrayList<DescentCtfeVariable>();
		
		DescentCtfeStackFrame sf = getStackFrame(stackFrame);
		if (sf == null)
			return new IVariable[0];
		
		InterState is = sf.getInterState();
		Scope scope = sf.getScope();		
		fillVariables(stackFrame, scope, vars);
		if (is != null) {
			fillVariables(stackFrame, is, vars);
		}
		return vars.toArray(new DescentCtfeVariable[vars.size()]);
	}

	private void fillVariables(int stackFrame, Scope currentScope, List<DescentCtfeVariable> vars) {
		if (currentScope.scopesym != null && currentScope.scopesym.symtab != null) {
			fillVariables(stackFrame, currentScope.scopesym.symtab, vars);
		}
		
		if (currentScope.enclosing != null) {
			fillVariables(stackFrame, currentScope.enclosing, vars);
		}
	}
	
	private void fillVariables(int stackFrame, InterState is, List<DescentCtfeVariable> vars) {
		if (is.vars != null) {
			for(Dsymbol dsymbol : is.vars) {
				DescentCtfeVariable var = toVariable(stackFrame, dsymbol);
				if (var == null)
					continue;
				
				vars.add(var);
			}
		}
		
		if (is.fd != null && is.fd.localsymtab != null) {
			fillVariables(stackFrame, is.fd.localsymtab, vars);
		}
	}
	
	private void fillVariables(int stackFrame, DsymbolTable symtab, List<DescentCtfeVariable> vars) {
		for(char[] key : symtab.keys()) {
			if (key == null)
				continue;
			
			Dsymbol dsymbol = symtab.lookup(key);
			DescentCtfeVariable var = toVariable(stackFrame, dsymbol);
			if (var == null)
				continue;
			
			vars.add(var);
		}
	}
	
	private DescentCtfeVariable toVariable(int stackFrame, Dsymbol dsymbol) {
		if (dsymbol instanceof VarDeclaration) {
			VarDeclaration var = (VarDeclaration) dsymbol;
			if (var.value != null) {
				return newVariable(0, var.ident.toString(), var.value);
			} else if (var.init != null) {
				if (var.init instanceof ExpInitializer) {
					return newVariable(0, var.ident.toString(), ((ExpInitializer) var.init).exp);
				}
			}
		} else if (dsymbol instanceof AliasDeclaration) {
			AliasDeclaration alias = (AliasDeclaration) dsymbol;
			
			if (alias.aliassym != null) {
				return newVariable(stackFrame, alias.ident.toString(), alias.aliassym.ident.toString());
			} else if (alias.type != null){
				return newVariable(stackFrame, alias.ident.toString(), alias.type.toString());
			}
		}
		return null;
	}

	public IStackFrame[] getStackFrames() {
		if (!fStackFrames.isEmpty()) {
			fStackFrames.remove(0);
			fStackFrames.add(0, newStackFrame());
		}
		
		return fStackFrames.toArray(new IStackFrame[fStackFrames.size()]);
	}
	
	private DescentCtfeStackFrame getStackFrame(int stackFrame) {
		int index = fStackFrames.size() - stackFrame - 1;
		if (index < 0 || index >= fStackFrames.size())
			return null;
		
		return fStackFrames.get(index);
	}
	
	protected DescentCtfeVariable newVariable(int stackFrame, String name, Expression value) {
		return new DescentCtfeVariable(fDebugTarget, this, 0, name, value);
	}
	
	protected DescentCtfeVariable newVariable(int stackFrame, String name, String value) {
		return new DescentCtfeVariable(fDebugTarget, this, 0, name, new StringExp(Loc.ZERO, value.toCharArray()));
	}
	
	private DescentCtfeStackFrame newStackFrame() {
		return fDebugTarget.newStackFrame(fCurrentUnit.getFullyQualifiedName(), fStackFrames.size(), fCurrentUnit, fCurrentLine, fCurrentScope, fCurrentInterState);
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
	
	private Document getDocument(ICompilationUnit unit) {
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
	
	private void bug(Exception e) {
		fOutput.error("Descent Bug, please report it ;-): " + e.getMessage());
		for(StackTraceElement elem : e.getStackTrace()) {
			fOutput.error(elem.toString());
		}
	}

}
