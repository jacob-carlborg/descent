package descent.internal.core.ctfe;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

import descent.core.ICompilationUnit;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnitResolver;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Scope;
import descent.internal.core.CompilationUnit;

public class CtfeDebugger implements IDebugger {

	private ResourceSearch fSearch;
	private final CompilationUnit fUnit;
	private final int fOffset;
	private Thread fThread;
	
	private List<Breakpoint> breakpoints = new ArrayList<Breakpoint>();
	private DescentCtfeDebugTarget fDebugTarget;
	private int fStartedDebugging;
	
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
			}
		};
		
		fThread = new Thread(runnable);
		fThread.start();
	}

	public void terminate() {
		try {
			fDebugTarget.terminate();
		} catch (DebugException e) {
		}
	}

	public void stepBegin(ASTDmdNode node, Scope sc) {
		ICompilationUnit unit = getCompilationUnit(sc);
		if (unit == null) return;
		
		if (isTarget(node, unit)) {
			fStartedDebugging++;
			System.out.println("Begin");
		}
		
		if (fStartedDebugging == 0)
			return;

		try {
			IDocument doc = new Document(unit.getSource());
			int line = doc.getLineOfOffset(node.start) + 1;
			
			if (hasBreakpoint(unit, line)) {
				fDebugTarget.breakpointHit(unit.getResource(), line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stepEnd(ASTDmdNode node, Scope sc) {
		ICompilationUnit unit = getCompilationUnit(sc);
		if (unit == null) return;
		
		if (isTarget(node, unit)) {
			fStartedDebugging--;
			if (fStartedDebugging == 0) {
				System.out.println("End");
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

}
