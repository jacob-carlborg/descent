package descent.core.ctfe;

import org.eclipse.debug.core.model.IStackFrame;

import descent.core.ICompilationUnit;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Scope;

public interface IDescentStackFrame extends IStackFrame {
	
	int getNumber();

	InterState getInterState();

	Scope getScope();
	
	ICompilationUnit getCompilationUnit();

}
