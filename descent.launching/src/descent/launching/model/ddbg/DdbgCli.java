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
	
	private final static boolean DEBUG = false;
	
	private int fTimeout;
	private boolean fshowBaseMembersInSameLevel;
	
	ICliRequestor fCliRequestor;
	IDescentDebugElementFactory fFactory;
	
	private IState fState;
	private IState fRunningState = new Running(this);
	private IStreamsProxy fProxy;

	private Object fWaitLock = new Object();
	private volatile boolean fWaitLockUsed;

	public DdbgCli() {
		setState(fRunningState);
	}
	
	public boolean isSingleThread() {
		return true;
	}
	
	public String getEndCommunicationString() {
		return "->";
	}
	
	public void initialize(ICliRequestor requestor, IDescentDebugElementFactory factory, IStreamsProxy out, int timeout, boolean showBaseMembersInSameLevel) {
		this.fCliRequestor = requestor;
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
		
		if (DEBUG) {
			System.out.println("~" + text);
		}
		
		fState.interpret(text);
	}
	
	public void start() throws DebugException, IOException {
		// Turn off recursive expression evaluation
		try {
			setState(new TogglingRecursiveExpressionEvaluation(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("er\n");
			
			waitStateReturn();
		} finally {
			setState(fRunningState);
		}
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

	public void addBreakpoint(IResource resource, int lineNumber) throws IOException {
		try {
			setState(new AddingBreakpoint(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("bp ");
			fProxy.write(resource.getLocation().toOSString());
			fProxy.write(":");
			fProxy.write(String.valueOf(lineNumber));
			fProxy.write("\n");
			
			waitStateReturn();
		} finally {
			setState(fRunningState);
		}
	}

	public void removeBreakpoint(IResource resource, int lineNumber) throws IOException {
		try {
			setState(new RemovingBreakpoint(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("dbp ");
			fProxy.write(resource.getLocation().toOSString());
			fProxy.write(":");
			fProxy.write(String.valueOf(lineNumber));
			fProxy.write("\n");
			
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
		
		if (DEBUG) {
			System.out.println("*getStackFrames()");
		}
		
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
			setState(new SettingStackFrame(this));
			
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
			setState(new ConsultingVariables(this, fshowBaseMembersInSameLevel));
			
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
	
	public IDescentVariable evaluateExpression(int stackFrame, String expression) throws IOException {
		setStackFrame(stackFrame);

		try {
			setState(new EvaluatingExpression(this, expression, fshowBaseMembersInSameLevel));
			
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
	
	private IDescentVariable[] ddbgVariablesToDescentVariables(List<DdbgVariable> ddbgVars, int stackFrame) {
		IDescentVariable[] vars = new IDescentVariable[ddbgVars.size()];
		for(int i = 0; i < ddbgVars.size(); i++) {
			vars[i] = ddbgVariableToDescentVariable(ddbgVars.get(i), stackFrame);
		}
		return vars;
	}
	
	private IDescentVariable ddbgVariableToDescentVariable(DdbgVariable ddbgVar, int stackFrame) {
		IDescentVariable var;
		if (ddbgVar.isLazy()) {
			var = fFactory.newLazyVariable(stackFrame, ddbgVar.getName(), ddbgVar.getValue(), ddbgVar.getExpression());
		} else {
			var = fFactory.newVariable(stackFrame, ddbgVar.getName(), ddbgVar.getValue());
		}
		
		if (fshowBaseMembersInSameLevel) {
			addVariablesChildren(var, ddbgVar.getChildren(), stackFrame);
		} else {
			var.addChildren(ddbgVariablesToDescentVariables(ddbgVar.getChildren(), stackFrame));
		}
		
		return var;
	}
	
	private void addVariablesChildren(IDescentVariable var, List<DdbgVariable> children, int stackFrame) {
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
