package descent.internal.launching.model;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;

import descent.launching.IDescentLaunchConfigurationConstants;
import descent.launching.model.ICli;

public class DescentRegisterGroup extends DebugElement implements IRegisterGroup {
	
	private final int fStackFrame;
	private final ICli fCli;
	private IRegister[] fRegisters;
	
	public DescentRegisterGroup(DescentDebugTarget target, ICli interpter, int stackFrame) {
		super(target);
		this.fCli = interpter;
		this.fStackFrame = stackFrame;
	}

	public String getName() throws DebugException {
		return "Registers";
	}

	public IRegister[] getRegisters() throws DebugException {
		if (fRegisters == null) {
			try {
				fRegisters = fCli.getRegisters(fStackFrame, this);
			} catch (IOException e) {
				e.printStackTrace();
				return new IRegister[0];
			}
		}
		return fRegisters;
	}

	public boolean hasRegisters() throws DebugException {
		return true;
	}

	public String getModelIdentifier() {
		return IDescentLaunchConfigurationConstants.ID_D_DEBUG_MODEL;
	}
	
	@Override
	public String toString() {
		try {
			return getName();
		} catch (DebugException e) {
			return super.toString();
		}
	}

}
