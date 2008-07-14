package descent.building;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Interface which should be implemented by all builder providers. Provides a
 * mechanism for building a D application, library, dynamic library or associated
 * resource(s). Builders may be associated with one or more run configurations
 * or as part of the Eclipse build cycle, as well as being invoked manually, so
 * there is no guarantee that it is actually being invoked within a launch despite
 * the fact that all configurations are done using a {@link ILaunchConfiguration}.
 * 
 * Implementations must have a no-element constructor unless they follow the
 * design pattern laid out in 
 * {@link org.eclipse.core.runtime.IConfigurationElement#createExecutableExtension(String)}
 * A new builder instance will be constructed on each build. The constructor should
 * run fairly quickly, as there is no progress monitor running during this phase
 * of the build.
 * 
 * @author Robert Fraser
 */
public interface IDBuilder
{
    public String build(ILaunchConfiguration config, ILaunch launch,
            IProgressMonitor pm);
}
