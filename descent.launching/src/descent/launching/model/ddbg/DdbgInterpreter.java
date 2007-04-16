package descent.launching.model.ddbg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.IVariable;

import descent.launching.model.DescentDebugTarget;
import descent.launching.model.DescentRegister;
import descent.launching.model.DescentStackFrame;
import descent.launching.model.DescentThread;
import descent.launching.model.DescentVariable;

public class DdbgInterpreter {

	private final DescentDebugTarget fTarget;
	private final IStreamsProxy proxy;

	private boolean fConsultingStackFrames;
	private List<IStackFrame> fStackFrames;
	private DescentThread fThread;

	private boolean fConsultingRegisters;
	private List<IRegister> fRegisters;
	private IRegisterGroup fRegisterGroup;
	
	private boolean fConsultingVariables;
	private List<IVariable> fVariables;

	private Object fWaitLock = new Object();
	private ILock fOperationLock = Platform.getJobManager().newLock();

	public DdbgInterpreter(DescentDebugTarget target, IStreamsProxy proxy) {
		this.fTarget = target;
		this.proxy = proxy;
	}

	public void interpret(String text, DescentDebugTarget handler)
			throws DebugException, IOException {
		if (fConsultingStackFrames) {
			if (text.equals("->")) {
				fConsultingStackFrames = false;
				synchronized (fWaitLock) {
					fWaitLock.notify();
				}
				return;
			} else if (text.startsWith("#")) {
				fStackFrames.add(new DescentStackFrame(this, fThread, text));
				return;
			}
		}

		if (fConsultingRegisters) {
			if (text.equals("->")) {
				fConsultingRegisters = false;
				synchronized (fWaitLock) {
					fWaitLock.notify();
				}
				return;
			} else {
				parseRegisters(text, fRegisterGroup);
				return;
			}
		}
		
		if (fConsultingVariables) {
			if (text.equals("->")) {
				fConsultingVariables = false;
				synchronized (fWaitLock) {
					fWaitLock.notify();
				}
				return;
			} else {
				parseVariable(text);
				return;
			}
		}

		if (text.startsWith("Breakpoint set")) {
			// Ignore
		} else if (text.startsWith("Breakpoint ")) {
			// Breakpoint n hit at file:lineNumber address
			handler.suspended(DebugEvent.BREAKPOINT);
		} else if (text.equals("Process terminated")) {
			handler.terminated();
		}
	}

	public void resume() throws IOException {
		beginOperation();

		try {
			proxy.write("r");
			proxy.write("\n");
		} finally {
			endOperation();
		}
	}

	public void terminate() throws IOException {
		beginOperation();

		try {
			proxy.write("q");
			proxy.write("\n");
		} finally {
			endOperation();
		}
	}

	public void addBreakpoint(IResource resource, int lineNumber,
			IStreamsProxy proxy) throws IOException {
		beginOperation();

		try {
			proxy.write("bp ");
			proxy.write(resource.getLocation().toOSString());
			proxy.write(":");
			proxy.write(String.valueOf(lineNumber));
			proxy.write("\n");
		} finally {
			endOperation();
		}
	}

	public void removeBreakpoint(IResource resource, int lineNumber,
			IStreamsProxy proxy) throws IOException {
		beginOperation();

		try {
			proxy.write("dbp ");
			proxy.write(resource.getLocation().toOSString());
			proxy.write(":");
			proxy.write(String.valueOf(lineNumber));
			proxy.write("\n");
		} finally {
			endOperation();
		}
	}

	public void stepOver() throws IOException {
		beginOperation();

		try {
			proxy.write("ov");
			proxy.write("\n");
		} finally {
			endOperation();
		}
	}

	public void stepInto() throws IOException {
		beginOperation();

		try {
			proxy.write("in");
			proxy.write("\n");
		} finally {
			endOperation();
		}
	}

	public void stepReturn() throws IOException {
		beginOperation();

		try {
			proxy.write("out");
			proxy.write("\n");
		} finally {
			endOperation();
		}
	}

	public IStackFrame[] getStackFrames(DescentThread thread)
			throws IOException {
		beginOperation();

		try {
			fConsultingStackFrames = true;
			fStackFrames = new ArrayList<IStackFrame>();
			fThread = thread;
			proxy.write("us");
			proxy.write("\n");
			try {
				synchronized (fWaitLock) {
					fWaitLock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return fStackFrames.toArray(new IStackFrame[fStackFrames.size()]);
		} finally {
			endOperation();
		}
	}

	public void setStackFrame(int stackFrame) throws IOException {
		beginOperation();

		try {
			proxy.write("f ");
			proxy.write(String.valueOf(stackFrame));
			proxy.write("\n");
		} finally {
			endOperation();
		}
	}

	public IRegister[] getRegisters(int stackFrame, IRegisterGroup registerGroup)
			throws IOException {
		setStackFrame(stackFrame);

		beginOperation();

		try {
			fConsultingRegisters = true;
			fRegisters = new ArrayList<IRegister>();
			fRegisterGroup = registerGroup;
			proxy.write("dr");
			proxy.write("\n");

			try {
				synchronized (fWaitLock) {
					fWaitLock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			IRegister[] registers = fRegisters.toArray(new IRegister[fRegisters.size()]);
			Arrays.sort(registers);
			return registers;
		} finally {
			endOperation();
		}
	}
	
	public IVariable[] getVariables(int stackFrame) throws IOException {
		setStackFrame(stackFrame);

		beginOperation();

		try {
			fConsultingVariables = true;
			fVariables = new ArrayList<IVariable>();
			proxy.write("lsv");
			proxy.write("\n");

			try {
				synchronized (fWaitLock) {
					fWaitLock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			IVariable[] variables = fVariables.toArray(new IVariable[fVariables
					.size()]);
			return variables;
		} finally {
			endOperation();
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
			fRegisters.add(new DescentRegister(fTarget, group, name, value));
		}
	}
	
	private void parseVariable(String text) {
		int indexOfEquals = text.indexOf('=');
		if (indexOfEquals == -1) return;
		
		String name = text.substring(0, indexOfEquals).trim();
		String value = text.substring(indexOfEquals + 1).trim();
		fVariables.add(new DescentVariable(fTarget, name, value));
	}

	private void beginOperation() {
		sleep();
		fOperationLock.acquire();
	}

	private void endOperation() {
		fOperationLock.release();
	}

	private void sleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
