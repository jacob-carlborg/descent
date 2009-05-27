package descent.core.ctfe;

import descent.core.ICompilationUnit;


public interface IDebuggerListener {
	
	void started();
	
	void stepEnded();
	
	void breakpointHit(ICompilationUnit unit, int lineNumber);
	
	void resumed(int detail);
	
	void terminated();

}
