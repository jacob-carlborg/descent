package descent.internal.debug.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import descent.debug.core.IDebuggerDescriptor;
import descent.debug.core.model.IDebugger;

public class DebuggerDescriptor implements IDebuggerDescriptor {
	
	private String id;
	private String name;
	private IConfigurationElement configurationElement;
	
	public DebuggerDescriptor(IConfigurationElement configurationElement) {
		this.configurationElement = configurationElement;
		this.id = configurationElement.getAttribute("id"); //$NON-NLS-1$
		this.name = configurationElement.getAttribute("name"); //$NON-NLS-1$
	}

	public IDebugger createDebugger() throws CoreException {
		return (IDebugger) configurationElement.createExecutableExtension("class"); //$NON-NLS-1$
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
