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
 * <b>This class is thread-safe.</b>
 * <b>This class is NOT intended to be implemented by clients.</b>
 * 
 * @see ICompileManager
 * @author Robert Fraser
 */
public interface IBuildManager
{
    /**
     * Adds the given command to the execution queue
     * 
     * @param cmd         the system command to execute. Must not be null
     * @param interpreter the response interpreter. If null, responses will be ignored.
     * @param workingDir  the working directory to execute the command. If null, the output directory of the project (where
     *                    object files go).
     * @param env         extensions/replacements for environment variables. If null, no extensions will be used.
     * @return            a future for the response code of the program.
     */
    public Future<Integer> exec(String cmd, IResponseInterpreter interpreter, String workingDir, String[] env);
    
    /**
     * Same as {@link #execSync(String, IResponseInterpreter, String, String[])} called as
     * <code>execSync(cmd, interpreter, null, null);
     */
    public Future<Integer> exec(String cmd, IResponseInterpreter interpreter);
    
    /**
     * Waits until the execution queue is finished
     */
    public void waitExecutionQueue();
    
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
