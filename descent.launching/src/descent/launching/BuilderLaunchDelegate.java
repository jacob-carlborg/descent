package descent.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

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
        // TODO get the builder type & perform the build
    }
}
