package descent.launching.model;

import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.IVariable;

public interface ICli {
	
	void initialize(ICliRequestor requestor, IDescentDebugElementFactory factory, IStreamsProxy out);
	
	void interpret(String text) throws DebugException, IOException;
	
	void resume() throws DebugException, IOException;
	
	void terminate() throws DebugException, IOException;
	
	void addBreakpoint(IResource resource, int lineNumber) throws DebugException, IOException;
	
	void removeBreakpoint(IResource resource, int lineNumber) throws DebugException, IOException;
	
	IStackFrame[] getStackFrames() throws DebugException, IOException;
	
	void setStackFrame(int stackFrameNumber) throws DebugException, IOException;
	
	IRegister[] getRegisters(int stackFrameNumber, IRegisterGroup registerGroup) throws IOException;
	
	IVariable[] getVariables(int stackFrameNumber) throws IOException;
	
	void stepOver() throws IOException;
	
	void stepInto() throws IOException;
	
	void stepReturn() throws IOException;

	IDescentVariable evaluateExpression(int stackFrameNumber, String expression) throws IOException;

}