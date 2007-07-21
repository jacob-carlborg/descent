package descent.internal.debug.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import descent.debug.core.DescentDebugPlugin;
import descent.debug.core.IDebuggerDescriptor;
import descent.debug.core.IDebuggerRegistry;

public class DebuggerRegistry implements IDebuggerRegistry {
	
	private IDebuggerDescriptor[] fDescriptors;
	
	public DebuggerRegistry() {
	}

	public IDebuggerDescriptor findDebugger(String id) {
		if (fDescriptors == null) {
			readDescriptors();
		}
		
		for(IDebuggerDescriptor descriptor : fDescriptors) {
			if (id.equals(descriptor.getId())) {
				return descriptor;
			}
		}
		
		return null;
	}

	public IDebuggerDescriptor[] getDebuggers() {
		if (fDescriptors == null) {
			readDescriptors();
		}
		return fDescriptors;
	}

	private void readDescriptors() {
		List<IDebuggerDescriptor> descriptors = new ArrayList<IDebuggerDescriptor>();
		
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(DescentDebugPlugin.PLUGIN_ID, "debuggers");
		IExtension[] extensions = point.getExtensions();
		
		for(IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for(IConfigurationElement element : elements) {
				descriptors.add(new DebuggerDescriptor(element));
			}
		}
		
		fDescriptors = descriptors.toArray(new IDebuggerDescriptor[descriptors.size()]);
	}

}
