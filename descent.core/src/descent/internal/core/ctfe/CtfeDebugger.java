package descent.internal.core.ctfe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;
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
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.DsymbolTable;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.core.CompilationUnit;

public class CtfeDebugger implements IDebugger {

	private ResourceSearch fSearch;
	private final CompilationUnit fUnit;
	private final int fOffset;
	private Thread fThread;
	
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
//	private List<IVariable[]> fVariables = new ArrayList<IVariable[]>();
	
	/**
	 * This is the next stack frame where the user wants to be.
	 * If it says: step over, it reamins the same.
	 * If it says: step into, it is one + current number of stack frames.
	 * If it says: step out, it it one - current number of stack frames.
	 * If it says: continue, it is -1.
	 */
	private int fNextStackFrame = -1;
	
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

	public CtfeDebugger(CompilationUnit unit, int offset) {
		this.fUnit = unit;
		this.fOffset = offset;
		this.fSearch = new ResourceSearch();
		this.fSemaphore = new Semaphore(0);
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
					
					CompilationUnitResolver.resolve(fParseResult, fUnit.getJavaProject(), null, CtfeDebugger.this);
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
				
				try {
					fDebugTarget.terminate();
				} catch (DebugException e) {
					e.printStackTrace();
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
		
		if (fStartedDebugging == 0) {
			if (isTarget(node, unit)) {
				fStartedDebugging++;
			} else {
				return;
			}
		}
		
		if (fStartedDebugging <= 0)
			return;
		
		try {
			IDocument doc = new Document(unit.getSource());
			int line = doc.getLineOfOffset(node.start) + 1;
			
			if (line == fCurrentLine && fStackFrames.size() == fCurrentStackFrame)
				return;
			
			fCurrentLine = line;
			fCurrentUnit = unit;
			
			if (fStackFrames.isEmpty()) {
				enterStackFrame();
				fNextStackFrame = 0;
			}
			
			fCurrentStackFrame = fStackFrames.size();
			
			// Only hit breakpoint if the expected stack frame for the user
			// is the same as the current one
			if (fNextStackFrame >= fStackFrames.size() - 1) {
				fDebugTarget.breakpointHit(unit.getResource(), line);
				fSemaphore.acquire();
			} else {
				// See if we hit a breakpoint
				if (hasBreakpoint(unit, fCurrentLine)) {
					fDebugTarget.breakpointHit(unit.getResource(), line);
					fSemaphore.acquire();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void internalStepEnd(ASTDmdNode node) {
		ICompilationUnit unit = getCompilationUnit(fCurrentScope);
		if (unit == null) return;
		
		if (isTarget(node, unit)) {
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
	
	private boolean isTarget(ASTDmdNode node, ICompilationUnit unit) {
		return unit.equals(fUnit) && node.start <= fOffset && fOffset <= node.start + node.length;
	}

	private boolean hasBreakpoint(ICompilationUnit unit, int line) {
		for(Breakpoint b : breakpoints) {
			if (b.unit.equals(unit) && b.line == line)
				return true;
		}
		return false;
	}

	public void stepInto() {
		fNextStackFrame = fStackFrames.size();
		
		fSemaphore.release();
	}

	public void stepOver() {
		fNextStackFrame = fStackFrames.size() - 1;
		
		fSemaphore.release();
	}

	public void stepReturn() {
		fNextStackFrame = fStackFrames.size() - 2;
		
		fSemaphore.release();
	}
	
	public void resume() {
		fNextStackFrame = -1;
		
		fSemaphore.release();
	}

	public synchronized IVariable evaluateExpression(int stackFrame, String expression) {
		DescentCtfeStackFrame sf = getStackFrame(stackFrame);
		Scope scope = sf.getScope();
		
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
				Expression result = exp.semantic(scope, fParseResult.context);
				result = result.optimize(ASTDmdNode.WANTflags | ASTDmdNode.WANTvalue | ASTDmdNode.WANTinterpret, fParseResult.context);
				
				if (problems.size() > 0) {
					return newVariable(stackFrame, expression, problems.toString());
				} else {
					return newVariable(stackFrame, expression, result.toString());
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
		InterState is = sf.getInterState();
		if (is == null) {
			Scope scope = sf.getScope();		
			fillVariables(stackFrame, scope, vars);
		} else {
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
			if (var.isConst()) {
				return newVariable(0, var.ident.toString(), var.init.toString());	
			} else if (var.value != null) {
				return newVariable(0, var.ident.toString(), var.value.toString());
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
			System.out.println(123456);
		
		return fStackFrames.get(index);
	}
	
	private DescentCtfeVariable newVariable(int stackFrame, String name, String value) {
		return new DescentCtfeVariable(fDebugTarget, this, 0, name, value);
	}
	
	private DescentCtfeStackFrame newStackFrame() {
		return fDebugTarget.newStackFrame(fCurrentUnit.getFullyQualifiedName(), fStackFrames.size(), fCurrentUnit, fCurrentLine, fCurrentScope, fCurrentInterState);
	}
	
	public void enterStackFrame() {
		System.out.println("Enter stack frame");
		
		fStackFrames.add(0, newStackFrame());
	}
	
	public void exitStackFrame() {
		System.out.println("Exit stack frame");
		
		fStackFrames.remove(0);
	}

}
