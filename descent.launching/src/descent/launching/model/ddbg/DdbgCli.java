package descent.launching.model.ddbg;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.IVariable;

import descent.launching.model.ICli;
import descent.launching.model.ICliRequestor;
import descent.launching.model.IDescentDebugElementFactory;
import descent.launching.model.IDescentVariable;

public class DdbgCli implements ICli {
	
	IState fState;
	IState fDefaultState = new DefaultState(this);

	ICliRequestor fCliRequestor;
	IDescentDebugElementFactory fFactory;
	IStreamsProxy fProxy;

	Object fWaitLock = new Object();
	

	public DdbgCli() {
		setState(fDefaultState);
	}
	
	public boolean isSingleThread() {
		return true;
	}
	
	public String getEndCommunicationString() {
		return "->";
	}
	
	public void initialize(ICliRequestor requestor, IDescentDebugElementFactory factory, IStreamsProxy out) {
		this.fCliRequestor = requestor;
		this.fFactory = factory;
		this.fProxy = out;
	}
	
	void setState(IState state) {
		this.fState = state;
	}

	public void interpret(String text)
			throws DebugException, IOException {
		
		fState.interpret(text);
	}

	public void resume() throws IOException {
		beginOperation();

		try {
			fProxy.write("r\n");
		} finally {
			setState(fDefaultState);
			endOperation();
		}
	}

	public void terminate() throws IOException {
		beginOperation();

		try {
			fProxy.write("q\n");
		} finally {
			setState(fDefaultState);
			endOperation();
		}
	}

	public void addBreakpoint(IResource resource, int lineNumber) throws IOException {
		beginOperation();

		try {
			setState(new AddingBreakpoint(this));
			
			fProxy.write("bp ");
			fProxy.write(resource.getLocation().toOSString());
			fProxy.write(":");
			fProxy.write(String.valueOf(lineNumber));
			fProxy.write("\n");
			
			waitStateReturn();
		} finally {
			setState(fDefaultState);
			endOperation();
		}
	}

	public void removeBreakpoint(IResource resource, int lineNumber) throws IOException {
		beginOperation();

		try {
			setState(new RemovingBreakpoint(this));
			
			fProxy.write("dbp ");
			fProxy.write(resource.getLocation().toOSString());
			fProxy.write(":");
			fProxy.write(String.valueOf(lineNumber));
			fProxy.write("\n");
			
			waitStateReturn();
		} finally {
			setState(fDefaultState);
			endOperation();
		}
	}

	public void stepOver() throws IOException {
		step("ov", DebugEvent.STEP_OVER);
	}

	public void stepInto() throws IOException {
		step("in", DebugEvent.STEP_INTO);
	}

	public void stepReturn() throws IOException {
		step("out", DebugEvent.STEP_RETURN);
	}
	
	private void step(String cmd, int debugEvent) throws IOException {
		beginOperation();

		try {
			setState(new Stepping(this, debugEvent));
			
			fProxy.write(cmd + "\n");
			
			waitStateReturn();
		} finally {
			setState(fDefaultState);
			endOperation();
		}
	}

	public IStackFrame[] getStackFrames()
			throws IOException {
		beginOperation();

		try {
			setState(new ConsultingStackFrames(this));
			
			fProxy.write("us\n");
			
			waitStateReturn();
			
			List<IStackFrame> stackFrames = ((ConsultingStackFrames) fState).fStackFrames;
			return stackFrames.toArray(new IStackFrame[stackFrames.size()]);
		} finally {
			setState(fDefaultState);
			endOperation();
		}
	}

	public void setStackFrame(int stackFrame) throws IOException {
		beginOperation();

		try {
			setState(new SettingStackFrame(this));
			
			fProxy.write("f ");
			fProxy.write(String.valueOf(stackFrame));
			fProxy.write("\n");
			
			waitStateReturn();
		} finally {
			setState(fDefaultState);
			endOperation();
		}
	}

	public IRegister[] getRegisters(int stackFrame, IRegisterGroup registerGroup)
			throws IOException {
		// setStackFrame(stackFrame);

		beginOperation();

		try {
			setState(new ConsultingRegisters(this, registerGroup));
			
			fProxy.write("dr\n");
			
			waitStateReturn();
			
			List<IRegister> registers = ((ConsultingRegisters) fState).fRegisters;
			IRegister[] registersArray = registers.toArray(new IRegister[registers.size()]);
			Arrays.sort(registersArray);
			return registersArray;
		} finally {
			setState(fDefaultState);
			endOperation();			
		}
	}
	
	public IVariable[] getVariables(int stackFrame) throws IOException {
		setStackFrame(stackFrame);

		beginOperation();

		try {
			setState(new ConsultingVariables(this));
			
			fProxy.write("lsv\n");

			waitStateReturn();
			
			List<DdbgVariable> variables = ((ConsultingVariables) fState).fVariables;
			completeTypes(variables);
			return ddbgVariablesToDescentVariables(variables);
		} finally {
			setState(fDefaultState);
			endOperation();
		}
	}
	
	public byte[] getMemoryBlock(long startAddress, long length) throws IOException {
		beginOperation();

		try {
			setState(new ConsultingMemoryBlock(this, length));
			
			fProxy.write("dm ");
			fProxy.write(Long.toHexString(startAddress));
			fProxy.write(" ");
			fProxy.write(String.valueOf(length));
			fProxy.write("\n");

			waitStateReturn();
			
			return ((ConsultingMemoryBlock) fState).fBytes;
		} finally {
			setState(fDefaultState);
			endOperation();
		}
	}
	
	public IDescentVariable evaluateExpression(int stackFrame, String expression) throws IOException {
		setStackFrame(stackFrame);

		beginOperation();

		try {
			setState(new EvaluatingExpression(this, expression));
			
			fProxy.write("= ");
			fProxy.write(expression);
			fProxy.write("\n");
			
			waitStateReturn();
			
			DdbgVariable ddbgVar = ((EvaluatingExpression) fState).fVariable;
			completeType(ddbgVar);
			return ddbgVariableToDescentVariable(ddbgVar);
		} finally {
			setState(fDefaultState);
			endOperation();			
		}
	}
	
	public String getType(String expression) throws IOException {
		try {
			setState(new ConsultingType(this));
			
			fProxy.write("t ");
			fProxy.write(expression);
			fProxy.write("\n");
			
			waitStateReturn();
			
			return ((ConsultingType) fState).fType;
		} finally {
			setState(fDefaultState);
			endOperation();			
		}
	}
	
	private void completeTypes(List<DdbgVariable> variables) throws IOException {
		completeTypes(variables, "");
	}
	
	private void completeTypes(List<DdbgVariable> variables, String prefix) throws IOException {
		for(DdbgVariable var : variables) {
			completeType(var, prefix);			
		}
	}
	
	private void completeType(DdbgVariable var) throws IOException {
		completeType(var, "");
	}
	
	private void completeType(DdbgVariable var, String prefix) throws IOException {
		if (var.getValue() == null) {
			String name = prefix + var.getName();
			var.setValue(getType(name));
		}
		completeTypes(var.getChildren(), prefix + var.getName() + ".");
	}
	
	private IDescentVariable[] ddbgVariablesToDescentVariables(List<DdbgVariable> ddbgVars) {
		IDescentVariable[] vars = new IDescentVariable[ddbgVars.size()];
		for(int i = 0; i < ddbgVars.size(); i++) {
			vars[i] = ddbgVariableToDescentVariable(ddbgVars.get(i));
		}
		return vars;
	}
	
	private IDescentVariable ddbgVariableToDescentVariable(DdbgVariable ddbgVar) {
		IDescentVariable var = fFactory.newVariable(ddbgVar.getName(), ddbgVar.getValue());
		var.addChildren(ddbgVariablesToDescentVariables(ddbgVar.getChildren()));
		return var;
	}

	private void beginOperation() {
		sleep();
	}

	private void endOperation() {
	}
	
	void waitStateReturn() {
		try {
			synchronized (fWaitLock) {
				fWaitLock.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	void notifyStateReturn() {
		synchronized (fWaitLock) {
			fWaitLock.notify();
		}
	}

	private void sleep() {
		try {
			Thread.sleep(120);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
