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
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnitResolver;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Module;
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
	
	private ASTDmdNode fCurrentNode;
	private int fCurrentLine;
	private ICompilationUnit fCurrentUnit;
	private Scope fCurrentScope;
	
	private List<IStackFrame> fStackFrames = new ArrayList<IStackFrame>();
	private List<IVariable[]> fVariables = new ArrayList<IVariable[]>();
	
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
					CompilationUnitResolver.resolve(AST.D1, 
							fUnit,
							fUnit.getJavaProject(), 
							JavaCore.getOptions(), 
							null, 
							false, 
							false, 
							CtfeDebugger.this,
							new NullProgressMonitor());
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
		ICompilationUnit unit = getCompilationUnit(sc);
		if (unit == null) return;
		
		if (fStartedDebugging == 0) {
			if (isTarget(node, unit)) {
				fStartedDebugging++;
			} else {
				return;
			}
		}
		
		try {
			IDocument doc = new Document(unit.getSource());
			int line = doc.getLineOfOffset(node.start) + 1;
			
			if (line == fCurrentLine)
				return;
			
			fCurrentNode = node;
			fCurrentLine = line;
			fCurrentUnit = unit;
			fCurrentScope = sc;
			
			if (fStackFrames.isEmpty())
				enterStackFrame();
			
			fDebugTarget.breakpointHit(unit.getResource(), line);
			
			fSemaphore.acquire();
		} catch (Exception e) {
			e.printStackTrace();
		}

//		try {
//			IDocument doc = new Document(unit.getSource());
//			int line = doc.getLineOfOffset(node.start) + 1;
//			
//			if (hasBreakpoint(unit, line)) {
//				fDebugTarget.breakpointHit(unit.getResource(), line);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public void stepEnd(ASTDmdNode node, Scope sc) {
		ICompilationUnit unit = getCompilationUnit(sc);
		if (unit == null) return;
		
		if (isTarget(node, unit)) {
			fStartedDebugging--;
			if (fStartedDebugging == 0) {
				exitStackFrame();
			}
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
		fSemaphore.release();
	}

	public void stepOver() {
		fSemaphore.release();
	}

	public void stepReturn() {
		fSemaphore.release();
	}
	
	public void resume() {
		fSemaphore.release();
	}

	public IVariable evaluateExpression(int stackFrame, String expression) {
		return null;
	}

	public IVariable[] getVariables(int stackFrame) {
		while(fVariables.size() - 1 < stackFrame) {
			fVariables.add(null);
		}
		
		IVariable[] variables = fVariables.get(stackFrame);
		if (variables == null) {
			List<DescentCtfeVariable> vars = new ArrayList<DescentCtfeVariable>();
			fillVariables(fCurrentScope, vars);
			variables = vars.toArray(new DescentCtfeVariable[vars.size()]);
			fVariables.set(stackFrame, variables);
		}
		return variables;
	}

	private void fillVariables(Scope currentScope, List<DescentCtfeVariable> vars) {
		if (currentScope.scopesym != null && currentScope.scopesym.symtab != null) {
			for(char[] key : currentScope.scopesym.symtab.keys()) {
				if (key == null)
					continue;
				
				Dsymbol dsymbol = currentScope.scopesym.symtab.lookup(key);
				if (dsymbol instanceof VarDeclaration) {
					VarDeclaration var = (VarDeclaration) dsymbol;
					if (!var.isConst())
						continue;
					
					vars.add(new DescentCtfeVariable(fDebugTarget, this, 0, var.ident.toString(), var.init.toString()));	
				} else if (dsymbol instanceof AliasDeclaration) {
					AliasDeclaration alias = (AliasDeclaration) dsymbol;
					
					if (alias.aliassym != null) {
						vars.add(new DescentCtfeVariable(fDebugTarget, this, 0, alias.ident.toString(), alias.aliassym.ident.toString()));
					} else if (alias.type != null){
						vars.add(new DescentCtfeVariable(fDebugTarget, this, 0, alias.ident.toString(), alias.type.toString()));
					}
				}
			}
		}
		
		if (currentScope.enclosing != null) {
			fillVariables(currentScope.enclosing, vars);
		}
	}

	public IStackFrame[] getStackFrames() {
		if (!fStackFrames.isEmpty()) {
			fStackFrames.set(0, newStackFrame());
		}
		
		return fStackFrames.toArray(new IStackFrame[fStackFrames.size()]);
	}
	
	public void enterStackFrame() {
		fStackFrames.add(0, newStackFrame());
	}
	
	private IStackFrame newStackFrame() {
		return fDebugTarget.newStackFrame(fCurrentUnit.getFullyQualifiedName(), fStackFrames.size(), fCurrentUnit, fCurrentLine);
	}
	
	public void exitStackFrame() {
		fStackFrames.remove(0);
		fVariables.remove(0);
	}

}
