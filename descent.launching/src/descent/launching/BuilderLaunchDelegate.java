package descent.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import descent.internal.launching.LaunchingPlugin;

/**
 * The launch configuration delegate which should be used for all D builders.
 * This will discover the type of builder and invoke the builder's build
 * method to perform the actual building of the application.
 * 
 * @author Robert Fraser
 */
public final class BuilderLaunchDelegate implements 
    ILaunchConfigurationDelegate
{
    public void launch(ILaunchConfiguration config, String mode,
            ILaunch launch, IProgressMonitor pm) throws CoreException
    {
        // Get the builder and call its build method... pretty simple, actually
        String configTypeId = config.getType().getIdentifier();
        IDBuilderType builderType = BuilderRegistry.getInstance().getBuilderForLaunchConfigurationType(configTypeId);
        if(null == builderType)
            throw error("Could not find builder for launch configuration type " + configTypeId);
        
        IDBuilder builder = builderType.getBuilder();
        if(null == builder)
            throw error("Could not create builder for builder type " + builderType.getIdentifier());
        
        builder.build(config, pm);
    }
    
    private static CoreException error(String message)
    {
        return new CoreException(new Status(IStatus.ERROR, 
                LaunchingPlugin.PLUGIN_ID, message));
    }
}
