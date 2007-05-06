package descent.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import descent.launching.IDebuggerDescriptor;
import descent.launching.model.IDebugger;

public class DebuggerDescriptor implements IDebuggerDescriptor {
	
	private String id;
	private String name;
	private IConfigurationElement configurationElement;
	
	public DebuggerDescriptor(IConfigurationElement configurationElement) {
		this.configurationElement = configurationElement;
		this.id = configurationElement.getAttribute("id");
		this.name = configurationElement.getAttribute("name");
	}

	public IDebugger createDebugger() throws CoreException {
		return (IDebugger) configurationElement.createExecutableExtension("class");
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
