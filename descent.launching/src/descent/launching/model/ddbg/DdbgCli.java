package descent.launching.model.ddbg;

import java.io.IOException;
import java.util.ArrayList;
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
	
	private interface IState {
		
		void interpret(String text) throws DebugException, IOException;
		
	}
	
	private class DefaultState implements IState {

		public void interpret(String text) throws DebugException, IOException {
			if (text.equals("Process terminated")) {
				 fCliRequestor.terminated();
			} else if (text.startsWith("Breakpoint ")) {
				// Breakpoint n hit at file:lineNumber address
				fCliRequestor.suspended(DebugEvent.BREAKPOINT);
			}
		}
		
		@Override
		public String toString() {
			return "default";
		}
		
	}
	
	private class AddingBreakpoint implements IState {
	
		public void interpret(String text) throws DebugException, IOException {
			if ("->".equals(text)) {
				setState(new DefaultState());
			}
		}
		
		@Override
		public String toString() {
			return "adding breakpoing";
		}
		
	}
	
	private class RemovingBreakpoint implements IState {
		
		public void interpret(String text) throws DebugException, IOException {
			if ("->".equals(text)) {
				setState(new DefaultState());
			}
		}
		
		@Override
		public String toString() {
			return "removing breakpoint";
		}
		
	}

	private class SteppingOver implements IState {
		
		public void interpret(String text) throws DebugException, IOException {
			if ("->".equals(text)) {
				setState(new DefaultState());
			}
		}
		
		@Override
		public String toString() {
			return "stepping over";
		}
		
	}
	
	private class SteppingInto implements IState {
		
		public void interpret(String text) throws DebugException, IOException {
			if ("->".equals(text)) {
				setState(new DefaultState());
			}
		}
		
		@Override
		public String toString() {
			return "stepping into";
		}
		
	}
	
	private class SteppingReturn implements IState {
		
		public void interpret(String text) throws DebugException, IOException {
			if ("->".equals(text)) {
				setState(new DefaultState());
			}
		}
		
		@Override
		public String toString() {
			return "stepping return";
		}
		
	}
	
	private class SettingStackFrame implements IState {
		
		public void interpret(String text) throws DebugException, IOException {
			if ("->".equals(text)) {
				setState(new DefaultState());
			}
		}
		
		@Override
		public String toString() {
			return "setting stack frame";
		}
		
	}
	
	private class ConsultingStackFrames implements IState {
		
		public List<IStackFrame> fStackFrames = new ArrayList<IStackFrame>();
		
		public ConsultingStackFrames() {
		}
		
		public void interpret(String text) throws DebugException, IOException {
			if (text.equals("->")) {
				synchronized (fWaitLock) {
					fWaitLock.notify();
				}
				return;
			} else if (text.startsWith("#")) {
				fStackFrames.add(parseStackFrame(text));
				return;
			}
		}
		
		private IStackFrame parseStackFrame(String data) {
			String name = data;
			int lineNumber = -1;
			int number = -1;
			String sourceName = null;
				
			if (data.length() == 0 || data.charAt(0) != '#') {
				return fFactory.newStackFrame(name, number, sourceName, lineNumber);
			}
			
			// Some positions in the string
			int indexOfFirstSpace = data.indexOf(' ');
			int indexOfIn = data.indexOf(" in ");
			int indexOfFrom = data.indexOf(" from ");
			int indexOfAt = data.indexOf(" at ");
			int lastIndexOfColon = data.lastIndexOf(':');
			
			// Number
			number = Integer.parseInt(data.substring(1, indexOfFirstSpace));		
			
			// Name
			if (indexOfIn != -1 && indexOfFrom != -1 && indexOfIn < indexOfFrom) {
				name = data.substring(indexOfIn + 4, indexOfFrom + 1);
			} else if (indexOfIn != -1 && indexOfAt != -1 && indexOfIn < indexOfAt) {
				name = data.substring(indexOfIn + 4, indexOfAt + 1);
			} else {
				if (indexOfFirstSpace != -1) {
					if (indexOfAt != -1) {
						name = data.substring(indexOfFirstSpace + 1, indexOfAt + 1);
					} else {
						int indexOfSecondSpace = data.indexOf(' ', indexOfFirstSpace + 1);
						if (indexOfSecondSpace != -1) {
							name = data.substring(indexOfFirstSpace + 1, indexOfSecondSpace);
						}
					}
				}
			}
			
			name = name.trim();
			if (name.endsWith(" ()")) {
				name = name.substring(0, name.length() - 3) + "()";
			}
			
			
			// sourceName and lineNumber
			if (indexOfAt != -1 && lastIndexOfColon != -1) {
				sourceName = data.substring(indexOfAt + 4, lastIndexOfColon);
				lineNumber = Integer.parseInt(data.substring(lastIndexOfColon + 1));
			}
			
			return fFactory.newStackFrame(name, number, sourceName, lineNumber);
		}
		
		@Override
		public String toString() {
			return "consulting stack frame";
		}
		
	}
	
	private class ConsultingRegisters implements IState {
		
		public List<IRegister> fRegisters = new ArrayList<IRegister>();
		private final IRegisterGroup fRegisterGroup;
		
		public ConsultingRegisters(IRegisterGroup registerGroup) {
			this.fRegisterGroup = registerGroup;
		}
		
		public void interpret(String text) throws DebugException, IOException {
			if (text.equals("->")) {
				synchronized (fWaitLock) {
					fWaitLock.notify();
				}
			} else {
				parseRegisters(text, fRegisterGroup);
			}
		}
		
		private void parseRegisters(String text, IRegisterGroup group) {
			for (int i = 0; i < 4; i++) {
				String sub;
				if (i == 3) {
					sub = text.substring(13*i);
				} else {
					sub = text.substring(13*i, 13*(i + 1));
				}
				int indexOfEqual = sub.indexOf('=');
				String name = sub.substring(0, indexOfEqual).trim();
				String value = sub.substring(indexOfEqual + 1).trim();
				fRegisters.add(fFactory.newRegister(group, name, value));
			}
		}
		
		@Override
		public String toString() {
			return "consulting registers";
		}
		
	}
	
	private class ConsultingVariables implements IState {
		
		public List<IDescentVariable> fVariables = new ArrayList<IDescentVariable>();
		private IDescentVariable fVariable;

		public void interpret(String text) throws DebugException, IOException {
			if (text.equals("->")) {
				synchronized (fWaitLock) {
					fWaitLock.notify();
				}
			} else {
				parseVariable(text);
			}
		}
		
		/*
		 * Variables come in two flavors:
		 * var = value
		 * var = {
		 *    subValue1 = ...,
		 *    subValue2 = ...,
		 *    (recursively)
		 * }
		 */
		private void parseVariable(String text) {
			if ("}".equals(text.trim())) {
				fVariable = fVariable.getParent();
				return;
			}
			
			int indexOfEquals = text.indexOf('=');
			if (indexOfEquals == -1) return;
			
			String name = text.substring(0, indexOfEquals).trim();
			String value = text.substring(indexOfEquals + 1).trim();
			if ("{".equals(value.trim())) {
				IDescentVariable newVariable = fFactory.newVariable(name, null);
				if (fVariable == null) {
					fVariables.add(newVariable);
					fVariable = newVariable;
				} else {
					fVariable.addChild(newVariable);
					fVariable = newVariable;
				}
			} else {
				if (fVariable != null) {
					value = value.trim();
					if (value.length() > 0 && value.charAt(value.length() - 1) == ',') {
						value = value.substring(0, value.length() - 1);; 
					}
				}
				
				IDescentVariable newVariable = fFactory.newVariable(name, value);
				if (fVariable == null) {
					fVariables.add(newVariable);
				} else {
					fVariable.addChild(newVariable);
				}
			}
		}
		
		@Override
		public String toString() {
			return "consulting variables";
		}
		
	}
	
	private class EvaluatingExpression implements IState {

		public IDescentVariable fVariable;
		private final String fExpression;
		
		public EvaluatingExpression(String expression) {
			this.fExpression = expression;
		}

		public void interpret(String text) throws DebugException, IOException {
			if (text.equals("->")) {
				synchronized (fWaitLock) {
					fWaitLock.notify();
				}
			} else {
				parseVariable(text);
			}
		}

		private void parseVariable(String text) {
			if ("{".equals(text.trim())) {
				fVariable = fFactory.newVariable(fExpression, null);
				return;
			}
			
			if ("}".equals(text.trim())) {
				if (fVariable.getParent() != null) {
					fVariable = fVariable.getParent();
				}
				return;
			}
			
			int indexOfEquals = text.indexOf('=');
			if (indexOfEquals == -1) {
				fVariable = fFactory.newVariable(fExpression, text);
				return;
			}
			
			String name = text.substring(0, indexOfEquals).trim();
			String value = text.substring(indexOfEquals + 1).trim();
			if ("{".equals(value.trim())) {
				IDescentVariable newVariable = fFactory.newVariable(name, null);
				if (fVariable != null) {
					fVariable.addChild(newVariable);
				}
				fVariable = newVariable;
			} else {
				if (fVariable != null) {
					value = value.trim();
					if (value.length() > 0 && value.charAt(value.length() - 1) == ',') {
						value = value.substring(0, value.length() - 1);; 
					}
				}
				
				IDescentVariable newVariable = fFactory.newVariable(name, value);
				if (fVariable == null) {
					fVariable = newVariable;
				} else {
					fVariable.addChild(newVariable);
				}
			}
		}
		
	}
	
	private IState fState;

	private ICliRequestor fCliRequestor;
	private IDescentDebugElementFactory fFactory;
	private IStreamsProxy fProxy;

	private Object fWaitLock = new Object();

	public DdbgCli() {
		setState(new DefaultState());
	}
	
	public void initialize(ICliRequestor requestor, IDescentDebugElementFactory factory, IStreamsProxy out) {
		this.fCliRequestor = requestor;
		this.fFactory = factory;
		this.fProxy = out;
	}
	
	private void setState(IState state) {
		System.out.println("State: " + state);
		this.fState = state;
	}

	public void interpret(String text)
			throws DebugException, IOException {
		
		System.out.println(">" + text);
		fState.interpret(text);
	}

	public void resume() throws IOException {
		beginOperation();

		try {
			setState(new DefaultState());
			
			fProxy.write("r\n");		
			//fTarget.resumed(DebugEvent.UNSPECIFIED);
		} finally {
			endOperation();
		}
	}

	public void terminate() throws IOException {
		beginOperation();

		try {
			fProxy.write("q\n");
		} finally {
			endOperation();
		}
	}

	public void addBreakpoint(IResource resource, int lineNumber) throws IOException {
		beginOperation();

		try {
			setState(new AddingBreakpoint());
			
			fProxy.write("bp ");
			fProxy.write(resource.getLocation().toOSString());
			fProxy.write(":");
			fProxy.write(String.valueOf(lineNumber));
			fProxy.write("\n");
		} finally {
			endOperation();
		}
	}

	public void removeBreakpoint(IResource resource, int lineNumber) throws IOException {
		beginOperation();

		try {
			setState(new RemovingBreakpoint());
			
			fProxy.write("dbp ");
			fProxy.write(resource.getLocation().toOSString());
			fProxy.write(":");
			fProxy.write(String.valueOf(lineNumber));
			fProxy.write("\n");
		} finally {
			endOperation();
		}
	}

	public void stepOver() throws IOException {
		beginOperation();

		try {
			setState(new SteppingOver());
			
			fProxy.write("ov\n");
			//fTarget.suspended(DebugEvent.STEP_OVER);
		} finally {
			endOperation();
		}
	}

	public void stepInto() throws IOException {
		beginOperation();

		try {
			setState(new SteppingInto());
			
			fProxy.write("in\n");
			// fTarget.suspended(DebugEvent.STEP_INTO);
		} finally {
			endOperation();
		}
	}

	public void stepReturn() throws IOException {
		beginOperation();

		try {
			setState(new SteppingReturn());
			
			fProxy.write("out\n");
			// fTarget.suspended(DebugEvent.STEP_RETURN);
		} finally {
			endOperation();
		}
	}

	public IStackFrame[] getStackFrames()
			throws IOException {
		beginOperation();

		try {
			setState(new ConsultingStackFrames());
			fProxy.write("us\n");
			
			try {
				synchronized (fWaitLock) {
					fWaitLock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			List<IStackFrame> stackFrames = ((ConsultingStackFrames) fState).fStackFrames;
			return stackFrames.toArray(new IStackFrame[stackFrames.size()]);
		} finally {
			setState(new DefaultState());
			endOperation();
		}
	}

	public void setStackFrame(int stackFrame) throws IOException {
		beginOperation();

		try {
			setState(new SettingStackFrame());
			
			fProxy.write("f ");
			fProxy.write(String.valueOf(stackFrame));
			fProxy.write("\n");
		} finally {
			endOperation();
		}
	}

	public IRegister[] getRegisters(int stackFrame, IRegisterGroup registerGroup)
			throws IOException {
		setStackFrame(stackFrame);

		beginOperation();

		try {
			setState(new ConsultingRegisters(registerGroup));
			
			fProxy.write("dr\n");
			
			try {
				synchronized (fWaitLock) {
					fWaitLock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			List<IRegister> registers = ((ConsultingRegisters) fState).fRegisters;
			IRegister[] registersArray = registers.toArray(new IRegister[registers.size()]);
			Arrays.sort(registersArray);
			return registersArray;
		} finally {
			setState(new DefaultState());
			endOperation();			
		}
	}
	
	public IVariable[] getVariables(int stackFrame) throws IOException {
		setStackFrame(stackFrame);

		beginOperation();

		try {
			setState(new ConsultingVariables());
			
			fProxy.write("lsv\n");

			try {
				synchronized (fWaitLock) {
					fWaitLock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			List<IDescentVariable> variables = ((ConsultingVariables) fState).fVariables;
			return variables.toArray(new IVariable[variables.size()]);
		} finally {
			setState(new DefaultState());
			endOperation();
		}
	}
	
	public IDescentVariable evaluateExpression(int stackFrame, String expression) throws IOException {
		setStackFrame(stackFrame);

		beginOperation();

		try {
			setState(new EvaluatingExpression(expression));
			
			fProxy.write("= ");
			fProxy.write(expression);
			fProxy.write("\n");
			
			try {
				synchronized (fWaitLock) {
					fWaitLock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return ((EvaluatingExpression) fState).fVariable;
		} finally {
			setState(new DefaultState());
			endOperation();			
		}
	}

	private void beginOperation() {
		sleep();
	}

	private void endOperation() {
	}

	private void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
