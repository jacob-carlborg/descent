package descent.internal.launching.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;

import descent.launching.model.ICli;

public class DescentRegister extends DescentVariable implements IRegister, Comparable<DescentRegister> {

	private final IRegisterGroup fRegisterGroup;

	public DescentRegister(IDebugTarget target, ICli cli, IRegisterGroup registerGroup, String name, String value) {
		super(target, cli, -1, name, value);
		this.fRegisterGroup = registerGroup;		
	}

	public IRegisterGroup getRegisterGroup() throws DebugException {
		return fRegisterGroup;
	}
	
	public int compareTo(DescentRegister o) {
		try {
			return getName().compareTo(o.getName());
		} catch (DebugException e) {
			return 0;
		}
	}

}
