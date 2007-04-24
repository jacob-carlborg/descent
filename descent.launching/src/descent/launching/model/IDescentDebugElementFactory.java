package descent.launching.model;

import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;

public interface IDescentDebugElementFactory {
	
	IRegister newRegister(IRegisterGroup registerGroup, String name, String value);
	
	IDescentVariable newVariable(String name, String value);
	
	IStackFrame newStackFrame(String name, int number, String sourceName, int lineNumber);

}
