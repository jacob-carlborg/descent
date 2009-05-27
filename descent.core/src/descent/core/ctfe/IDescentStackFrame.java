package descent.core.ctfe;

import org.eclipse.debug.core.model.IStackFrame;

import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Scope;

public interface IDescentStackFrame extends IStackFrame {

	InterState getInterState();

	Scope getScope();

}
