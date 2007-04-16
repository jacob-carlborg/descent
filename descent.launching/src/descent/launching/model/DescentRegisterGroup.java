package descent.launching.model;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;

import descent.launching.IDescentLaunchConfigurationConstants;
import descent.launching.model.ddbg.DdbgInterpreter;

public class DescentRegisterGroup extends DebugElement implements IRegisterGroup {
	
	private final int stackFrame;
	private final DdbgInterpreter interpter;
	
	public DescentRegisterGroup(DescentDebugTarget target, DdbgInterpreter interpter, int stackFrame) {
		super(target);
		this.interpter = interpter;
		this.stackFrame = stackFrame;
	}

	public String getName() throws DebugException {
		return "Registers";
	}

	public IRegister[] getRegisters() throws DebugException {
		try {
			return interpter.getRegisters(stackFrame, this);
		} catch (IOException e) {
			e.printStackTrace();
			return new IRegister[0];
		}
	}

	public boolean hasRegisters() throws DebugException {
		return true;
	}

	public String getModelIdentifier() {
		return IDescentLaunchConfigurationConstants.ID_D_DEBUG_MODEL;
	}

}
