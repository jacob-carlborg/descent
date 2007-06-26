package descent.internal.launching.model.gdb;

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

public class GdbDebugger implements IDebugger {
	
	private int fTimeout;
	private boolean fshowBaseMembersInSameLevel;
	
	IDebuggerListener fListener;
	IDebugElementFactory fFactory;
	
	private IState fState;
	private IState fRunningState = new Running(this);
	private IStreamsProxy fProxy;

	private Object fWaitLock = new Object();
	private volatile boolean fWaitLockUsed;
	
	public List<String> getDebuggerCommandLineArguments() {
		List<String> args = new ArrayList<String>();
		args.add("-readnow");
		args.add("-fullname");
		return args;
	}
	
	public List<String> getDebugeeCommandLineArguments(String[] arguments) {
		List<String> args = new ArrayList<String>();
		args.add("-args");
		args.addAll(Arrays.asList(arguments));
		return args;
	}

	public void addBreakpoint(String filename, int lineNumber) throws DebugException, IOException {
		try {
			setState(new WaitingConfirmation(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("break ");
			fProxy.write(ArgumentUtils.toStringArgument(
					toGdbPath(filename) +
					":" +
					lineNumber
				));
			fProxy.write("\n");
			
			waitStateReturn();
		} finally {
			setState(fRunningState);
		}
	}

	public IVariable evaluateExpression(int stackFrame, String expression) throws IOException {
		setStackFrame(stackFrame);

		try {
			setState(new EvaluatingExpression(this, expression));
			
			beforeWaitStateReturn();
			
			fProxy.write("print " + expression + "\n");
			
			waitStateReturn();
			
			GdbVariable gdbVar = ((EvaluatingExpression) fState).fVariable;
			if (gdbVar == null) {
				return null;
			}
			
			completeType(gdbVar);
			return gdbVariableToDescentVariable(gdbVar, stackFrame);
		} finally {
			setState(fRunningState);
		}
	}

	public String getEndCommunicationString() {
		return "(gdb)";
	}

	public byte[] getMemoryBlock(long startAddress, long length) throws IOException {
		try {
			setState(new ConsultingMemoryBlock(this, length));
			
			beforeWaitStateReturn();
			
			fProxy.write("x/" + String.valueOf(length) + "b ");
			fProxy.write(String.valueOf(startAddress));
			fProxy.write("\n");

			waitStateReturn();
			
			return ((ConsultingMemoryBlock) fState).fBytes;
		} finally {
			setState(fRunningState);
		}
	}

	public IRegister[] getRegisters(IRegisterGroup registerGroup) throws IOException {
		try {
			setState(new ConsultingRegisters(this, registerGroup));
			
			beforeWaitStateReturn();
			
			fProxy.write("info all-registers\n");
			
			waitStateReturn();
			
			List<IRegister> registers = ((ConsultingRegisters) fState).fRegisters;
			IRegister[] registersArray = registers.toArray(new IRegister[registers.size()]);
			Arrays.sort(registersArray);
			return registersArray;
		} finally {
			setState(fRunningState);
		}
	}

	public IStackFrame[] getStackFrames() throws DebugException, IOException {
		try {
			setState(new ConsultingStackFrames(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("bt\n");
			
			waitStateReturn();
			
			List<IStackFrame> stackFrames = ((ConsultingStackFrames) fState).fStackFrames;
			return stackFrames.toArray(new IStackFrame[stackFrames.size()]);
		} finally {
			setState(fRunningState);
		}
	}

	public IVariable[] getVariables(int stackFrame) throws IOException {
		setStackFrame(stackFrame);
		
		List<GdbVariable> retVariables = new ArrayList<GdbVariable>();
		
		String[] args = { "args", "locals" };
		for(String arg : args) {
			try {
				setState(new ConsultingVariables(this));
				
				beforeWaitStateReturn();
				
				fProxy.write("info " + arg + "\n");
	
				waitStateReturn();
				
				retVariables.addAll(((ConsultingVariables) fState).fVariables);				
			} finally {
				setState(fRunningState);
			}
		}
		
		completeTypes(retVariables);
		return gdbVariablesToDescentVariables(retVariables, stackFrame);
	}

	public void initialize(IDebuggerListener listener, IDebugElementFactory factory, IStreamsProxy out, int timeout, boolean showBaseMembersInSameLevel) {
		this.fListener = listener;
		this.fFactory = factory;
		this.fProxy = out;
		this.fTimeout = timeout;
		this.fshowBaseMembersInSameLevel = showBaseMembersInSameLevel;
	}

	public void interpret(String text) throws DebugException, IOException {
		fState.interpret(text);
	}
	
	public void interpretError(String text) throws DebugException, IOException {
		fState.interpretError(text);
	}

	public void removeBreakpoint(String filename, int lineNumber) throws DebugException, IOException {
		try {
			setState(new WaitingConfirmation(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("clear ");
			fProxy.write(ArgumentUtils.toStringArgument(
					toGdbPath(filename) +
					":" +
					lineNumber
					));
			fProxy.write("\n");
			
			waitStateReturn();
		} finally {
			setState(fRunningState);
		}
	}

	public void resume() throws DebugException, IOException {
		try {
			fProxy.write("c\n");
		} finally {
			setState(fRunningState);
		}
	}

	public void setStackFrame(int stackFrame) throws IOException {
		try {
			setState(new WaitingConfirmation(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("frame ");
			fProxy.write(String.valueOf(stackFrame));
			fProxy.write("\n");
			
			waitStateReturn();
		} finally {
			setState(fRunningState);
		}
	}

	public void start() throws DebugException, IOException {
		try {
			fProxy.write("set print pretty on\n");
			fProxy.write("r\n");
		} finally {
			setState(fRunningState);
		}
	}

	public void stepOver() throws IOException {
		step("next", DebugEvent.STEP_OVER);
	}

	public void stepInto() throws IOException {
		step("step", DebugEvent.STEP_INTO);
	}

	public void stepReturn() throws IOException {
		step("finish", DebugEvent.STEP_RETURN);
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

	public void terminate() throws DebugException, IOException {
		try {
			fProxy.write("quit\n");
			fProxy.write("y\n");
		} finally {
			setState(fRunningState);
		}
	}
	
	public String getType(String expression) throws IOException {
		try {
			setState(new ConsultingType(this));
			
			beforeWaitStateReturn();
			
			fProxy.write("whatis ");
			fProxy.write(expression);
			fProxy.write("\n");
			
			waitStateReturn();
			
			return ((ConsultingType) fState).fType;
		} finally {
			setState(fRunningState);
		}
	}
	
	private void completeTypes(List<GdbVariable> variables) throws IOException {
		completeTypes(variables, "");
	}
	
	private void completeTypes(List<GdbVariable> variables, String prefix) throws IOException {
		for(GdbVariable var : variables) {
			completeType(var, prefix);			
		}
	}
	
	private void completeType(GdbVariable var) throws IOException {
		completeType(var, "");
	}
	
	private void completeType(GdbVariable var, String prefix) throws IOException {
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
	
	private IVariable[] gdbVariablesToDescentVariables(List<GdbVariable> ddbgVars, int stackFrame) {
		IVariable[] vars = new IParentVariable[ddbgVars.size()];
		for(int i = 0; i < ddbgVars.size(); i++) {
			vars[i] = gdbVariableToDescentVariable(ddbgVars.get(i), stackFrame);
		}
		return vars;
	}
	
	private IVariable gdbVariableToDescentVariable(GdbVariable ddbgVar, int stackFrame) {
		IParentVariable var;
		if (ddbgVar.isLazy()) {
			return fFactory.newLazyVariable(stackFrame, ddbgVar.getName(), ddbgVar.getValue(), ddbgVar.getExpression());
		} else {
			var = fFactory.newParentVariable(stackFrame, ddbgVar.getName(), ddbgVar.getValue());
		}
		
		if (fshowBaseMembersInSameLevel) {
			addVariablesChildren(var, ddbgVar.getChildren(), stackFrame);
		} else {
			var.addChildren(gdbVariablesToDescentVariables(ddbgVar.getChildren(), stackFrame));
		}
		
		return var;
	}
	
	private void addVariablesChildren(IParentVariable var, List<GdbVariable> children, int stackFrame) {
		// The first child may be the base clase
		if (children.size() > 0) {
			GdbVariable first = children.get(0);
			if (first.isBase()) {
				// Add the children's base
				addVariablesChildren(var, first.getChildren(), stackFrame);
				// Add the rest
				var.addChildren(gdbVariablesToDescentVariables(children.subList(1, children.size()), stackFrame));
			} else {
				var.addChildren(gdbVariablesToDescentVariables(children, stackFrame));
			}
		}
	}
	
	private String toGdbPath(String path) {
		return path.replace('\\', '/');
	}
	
	void setState(IState state) {
		this.fState = state;
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
