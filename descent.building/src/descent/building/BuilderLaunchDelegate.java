package descent.building;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import descent.internal.building.BuildingPlugin;
import descent.internal.building.debuild.DebuildMessages;

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
            throw error(String.format(DebuildMessages.BuilderLaunchDelegate_error_could_not_find_builder, configTypeId));
        
        IDBuilder builder = builderType.getBuilder();
        if(null == builder)
            throw error(String.format(DebuildMessages.BuilderLaunchDelegate_error_could_not_instantiate_builder, builderType.getIdentifier()));
        
        builder.build(config, launch, pm);
    }
    
    private static CoreException error(String message)
    {
        return new CoreException(new Status(IStatus.ERROR, 
                BuildingPlugin.PLUGIN_ID, message));
    }
}
