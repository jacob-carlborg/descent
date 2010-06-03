package descent.internal.core.ctfe;

import org.eclipse.jface.text.IDocument;

import descent.core.IProblemRequestor;
import descent.core.compiler.IProblem;

public class ProblemRequestor implements IProblemRequestor {
	
	private final Debugger debugger;
	
	public ProblemRequestor(Debugger debugger) {
		this.debugger = debugger;
	}
	
	public void acceptProblem(IProblem problem) {
		if (debugger.fCurrentUnit == null) {
			debugger.fOutput.error(problem.toString());
			return;
		}
		
		try {
			IDocument doc = debugger.getDocument(debugger.fCurrentUnit);
			int line = doc.getLineOfOffset(problem.getSourceStart()) + 1;
			
			debugger.fOutput.error(debugger.fCurrentUnit.getModuleName() + "(" + line + "): " + problem.toString());
			
			int oldLine = debugger.fCurrentLine;
			debugger.fCurrentLine = line;
			debugger.enterStackFrame(null);
			
			try {
				debugger.fListener.stepEnded();
				debugger.fSemaphore.acquire();
			} finally {
				debugger.exitStackFrame(null);
				debugger.fCurrentLine = oldLine;
			}
		} catch (Exception e) {
			debugger.bug(e);
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

}
