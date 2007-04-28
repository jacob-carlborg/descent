package descent.internal.launching.model;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;

import descent.launching.IDescentLaunchConfigurationConstants;
import descent.launching.model.ICli;

public class DescentRegisterGroup extends DebugElement implements IRegisterGroup {
	
	private final ICli fCli;
	
	private boolean fRegistersInvalid;
	private IRegister[] fRegisters;
	
	public DescentRegisterGroup(DescentDebugTarget target, ICli interpter) {
		super(target);
		this.fCli = interpter;
	}

	public String getName() throws DebugException {
		return "Registers";
	}

	public IRegister[] getRegisters() throws DebugException {
		try {
			if (fRegisters == null || fRegistersInvalid) {
				IRegister[] newRegisters = fCli.getRegisters(this);
				fRegisters = mergeRegisters(fRegisters, newRegisters);
				fRegistersInvalid = false;
			}
			return fRegisters;
		} catch (IOException e) {
			e.printStackTrace();
			return new IRegister[0];
		}
	}

	private IRegister[] mergeRegisters(IRegister[] registers, IRegister[] newRegisters) throws DebugException {
		if (registers == null || registers.length != newRegisters.length) {
			return newRegisters;
		}
		
		for(int i = 0; i < registers.length; i++) {
			DescentRegister oldR = (DescentRegister) registers[i];
			DescentRegister newR = (DescentRegister) newRegisters[i];
			
			if (!oldR.getValue().getValueString().equals(newR.getValue().getValueString())) {
				newR.setHasValueChanged(true);
			}
		}
		return newRegisters;
	}

	public boolean hasRegisters() throws DebugException {
		return true;
	}
	
	public void invalidate() {
		fRegistersInvalid = true;
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
