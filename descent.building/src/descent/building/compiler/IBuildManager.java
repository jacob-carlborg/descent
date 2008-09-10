package descent.building.compiler;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.Future;

import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Class representing the state of the internal builder, which the 
 * {@link ICompileManager} should use to gain information on the state of the
 * build and to perform various tasks.
 * 
 * <b>This class is NOT intended to be implemented by clients.</b>
 * <b>This class is thread-safe.</b>
 * 
 * @see ICompileManager
 * @author Robert Fraser
 */
public interface IBuildManager
{
    /**
     * Gets the execution monitor used to managed running processes for this build
     * 
     * @return the execution monitor that can be used to manage running processes for this build
     */
    public IExecutionMonitor getExecutionMonitor();
    
    /**
     * Gets a new error reporter that can attach errors to projects, files, etc.
     * 
     * @return the error reporter to use for this build
     */
    public IErrorReporter getErrorReporter();
    
    /**
     * Gets the launch configuration associated with this build
     * 
     * @return the launch configuration associated with this build
     */
    public ILaunchConfiguration getLaunchConfiguration();
    
    /**
     * Gets project import paths that should be appended for thgis build
     * 
     * @return the import paths to append for this build
     */
    public Collection<File> getImportPaths();
}
