package descent.internal.launching.model.ddbg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.IVariable;

import descent.launching.model.IDebugElementFactory;
import descent.launching.model.IDebugger;
import descent.launching.model.IDebuggerListener;
import descent.launching.model.IParentVariable;
import descent.launching.utils.ArgumentUtils;

public class DdbgDebugger implements IDebugger {
	
	private int fTimeout;
	private boolean fshowBaseMembersInSameLevel;
	
	IDebuggerListener fListener;
	IDebugElementFactory fFactory;
	
	private IState fState;
	private IState fRunningState = new Running(this);
	private IStreamsProxy fProxy;

	private Object fWaitLock = new Object();
	private volatile boolean fWaitLockUsed;

	public DdbgDebugger() {
		setState(fRunningState);
	}
	
	public String getEndCommunicationString() {
		return "->";
	}
	
	public List<String> getDebuggerCommandLineArguments() {
		return new ArrayList<String>(0);
	}
	
	public List<String> getDebugeeCommandLineArguments(String[] arguments) {
		return Arrays.asList(arguments);
	}
	
	public void initialize(IDebuggerListener listener, IDebugElementFactory factory, IStreamsProxy out, int timeout, boolean showBaseMembersInSameLevel) {
		this.fListener = listener;
		this.fFactory = factory;
		this.fProxy = out;
		this.fTimeout = timeout;
		this.fshowBaseMembersInSameLevel = showBaseMembersInSameLevel;
	}
	
	void setState(IState state) {
		this.fState = state;
	}

	public void interpret(String text)
			throws DebugException, IOException {
		fState.interpret(text);
	}
	
	public void interpretError(String text) throws DebugException, IOException {
		// Nothing to do
	}
	
	public void start() throws DebugException, IOException {
		// Turn off recursive expression evaluation
		try {
			setState(new WaitingConfirmation(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("er\n");
			
			waitStateReturn();
		} finally {
			setState(fRunningState);
		}
		
		// Redirect debuggees output to new console
		try {
			setState(new WaitingConfirmation(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("nc\n");
			
			waitStateReturn();
		} finally {
			setState(fRunningState);
		}
		
		// And start
		resume();
	}

	public void resume() throws IOException {
		try {
			fProxy.write("r\n");
		} finally {
			setState(fRunningState);
		}
	}

	public void terminate() throws IOException {
		try {
			fProxy.write("q\n");
		} finally {
			setState(fRunningState);
		}
	}

	public void addBreakpoint(String filename, int lineNumber) throws IOException {
		try {
			setState(new WaitingConfirmation(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("bp " +
				ArgumentUtils.toStringArgument(filename) +
				":" + lineNumber + "\n"
				);
			
			waitStateReturn();
		} finally {
			setState(fRunningState);
		}
	}

	public void removeBreakpoint(String filename, int lineNumber) throws IOException {
		try {
			setState(new WaitingConfirmation(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("dbp " +
				ArgumentUtils.toStringArgument(filename) +
				":" +lineNumber + "\n"
				);
			
			waitStateReturn();
		} finally {
			setState(fRunningState);
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
		try {
			setState(new Stepping(this, debugEvent));
			
			beforeWaitStateReturn();
			
			fProxy.write(cmd + "\n");
			
			waitStateReturn();
		} finally {
			setState(fRunningState);
		}
	}

	public IStackFrame[] getStackFrames()
			throws IOException {
		
		try {
			setState(new ConsultingStackFrames(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("us\n");
			
			waitStateReturn();
			
			List<IStackFrame> stackFrames = ((ConsultingStackFrames) fState).fStackFrames;
			return stackFrames.toArray(new IStackFrame[stackFrames.size()]);
		} finally {
			setState(fRunningState);
		}
	}

	public void setStackFrame(int stackFrame) throws IOException {
		try {
			setState(new WaitingConfirmation(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("f ");
			fProxy.write(String.valueOf(stackFrame));
			fProxy.write("\n");
			
			waitStateReturn();
		} finally {
			setState(fRunningState);
		}
	}

	public IRegister[] getRegisters(IRegisterGroup registerGroup)
			throws IOException {
		try {
			setState(new ConsultingRegisters(this, registerGroup));
			
			beforeWaitStateReturn();
			
			fProxy.write("dr\n");
			
			waitStateReturn();
			
			List<IRegister> registers = ((ConsultingRegisters) fState).fRegisters;
			IRegister[] registersArray = registers.toArray(new IRegister[registers.size()]);
			Arrays.sort(registersArray);
			return registersArray;
		} finally {
			setState(fRunningState);
		}
	}
	
	public IVariable[] getVariables(int stackFrame) throws IOException {
		setStackFrame(stackFrame);

		try {
			setState(new ConsultingVariables(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("lsv\n");

			waitStateReturn();
			
			List<DdbgVariable> variables = ((ConsultingVariables) fState).fVariables;
			completeTypes(variables);
			return ddbgVariablesToDescentVariables(variables, stackFrame);
		} finally {
			setState(fRunningState);
		}
	}
	
	public byte[] getMemoryBlock(long startAddress, long length) throws IOException {
		try {
			setState(new ConsultingMemoryBlock(this, length));
			
			beforeWaitStateReturn();
			
			fProxy.write("dm ");
			fProxy.write(Long.toHexString(startAddress));
			fProxy.write(" ");
			fProxy.write(String.valueOf(length));
			fProxy.write("\n");

			waitStateReturn();
			
			return ((ConsultingMemoryBlock) fState).fBytes;
		} finally {
			setState(fRunningState);
		}
	}
	
	public IVariable evaluateExpression(int stackFrame, String expression) throws IOException {
		setStackFrame(stackFrame);

		try {
			setState(new EvaluatingExpression(this, expression));
			
			beforeWaitStateReturn();
			
			fProxy.write("= " + expression + "\n");
			
			waitStateReturn();
			
			DdbgVariable ddbgVar = ((EvaluatingExpression) fState).fVariable;
			completeType(ddbgVar);
			return ddbgVariableToDescentVariable(ddbgVar, stackFrame);
		} finally {
			setState(fRunningState);
		}
	}
	
	public String getType(String expression) throws IOException {
		try {
			setState(new ConsultingType(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("t ");
			fProxy.write(expression);
			fProxy.write("\n");
			
			waitStateReturn();
			
			return ((ConsultingType) fState).fType;
		} finally {
			setState(fRunningState);
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
		try {
			if (var.getValue() == null && !var.isBase()) {
				String name = prefix + var.getName();
				var.setValue(getType(name));
			} else if (var.isBase()) {
				var.setValue("");
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		prefix = var.isBase() ? prefix : prefix + var.getName() + ".";
		completeTypes(var.getChildren(), prefix);
	}
	
	private IVariable[] ddbgVariablesToDescentVariables(List<DdbgVariable> ddbgVars, int stackFrame) {
		IVariable[] vars = new IVariable[ddbgVars.size()];
		for(int i = 0; i < ddbgVars.size(); i++) {
			vars[i] = ddbgVariableToDescentVariable(ddbgVars.get(i), stackFrame);
		}
		return vars;
	}
	
	private IVariable ddbgVariableToDescentVariable(DdbgVariable ddbgVar, int stackFrame) {
		IParentVariable var;
		if (ddbgVar.isLazy()) {
			return fFactory.newLazyVariable(stackFrame, ddbgVar.getName(), ddbgVar.getValue(), ddbgVar.getExpression());
		} else {
			var = fFactory.newParentVariable(stackFrame, ddbgVar.getName(), ddbgVar.getValue());
		}
		
		if (fshowBaseMembersInSameLevel) {
			addVariablesChildren(var, ddbgVar.getChildren(), stackFrame);
		} else {
			var.addChildren(ddbgVariablesToDescentVariables(ddbgVar.getChildren(), stackFrame));
		}
		
		return var;
	}
	
	private void addVariablesChildren(IParentVariable var, List<DdbgVariable> children, int stackFrame) {
		// The first child may be the base clase
		if (children.size() > 0) {
			DdbgVariable first = children.get(0);
			if (first.isBase()) {
				// Add the children's base
				addVariablesChildren(var, first.getChildren(), stackFrame);
				// Add the rest
				var.addChildren(ddbgVariablesToDescentVariables(children.subList(1, children.size()), stackFrame));
			} else {
				var.addChildren(ddbgVariablesToDescentVariables(children, stackFrame));
			}
		}
	}
	
	void beforeWaitStateReturn() {
		fWaitLockUsed = false;
	}
	
	void waitStateReturn() {
		try {
			synchronized (fWaitLock) {
				if (!fWaitLockUsed) {
					fWaitLock.wait(fTimeout);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	void notifyStateReturn() {
		fWaitLockUsed = true;
		synchronized (fWaitLock) {
			fWaitLock.notify();
		}		
	}

}
