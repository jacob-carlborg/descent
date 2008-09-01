package descent.internal.building.debuild;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.Future;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.building.compiler.IBuildManager;
import descent.building.compiler.IErrorReporter;
import descent.building.compiler.IResponseInterpreter;

/* package */ final class BuildManager implements IBuildManager
{
    private final BuildRequest req;
    private final ErrorReporter err;
    private final Collection<File> importPath;
    private final ExecutionMonitor executor;
    
    private final String defaultWorkingDir;
    
    public BuildManager(BuildRequest req, ErrorReporter err, Collection<File> importPath,
            ILaunch launch)
    {
        this.req = req;
        this.err = err;
        this.importPath = importPath;
        this.executor = new ExecutionMonitor(launch);
        
        defaultWorkingDir = req.getOutputLocation().toString();
    }
    
    /* (non-Javadoc)
     * @see descent.building.compiler.IBuildManager#exec(java.lang.String, descent.building.compiler.IResponseInterpreter)
     */
    public Future<Integer> exec(String cmd, IResponseInterpreter interpreter)
    {
        return exec(cmd, interpreter, defaultWorkingDir, null);
    }
    
    /* (non-Javadoc)
     * @see descent.building.compiler.IBuildManager#exec(java.lang.String, descent.building.compiler.IResponseInterpreter, java.lang.String, java.lang.String[])
     */
    public Future<Integer> exec(String cmd, IResponseInterpreter interpreter,
            String workingDir, String[] env)
    {
        return executor.addTask(cmd, interpreter, env, workingDir);
    }
    
    /* (non-Javadoc)
     * @see descent.building.compiler.IBuildManager#waitExecutionQueue()
     */
    public void waitExecutionQueue()
    {
        executor.waitFor();
    }
    
    /* (non-Javadoc)
     * @see descent.building.compiler.IBuildManager#getErrorReporter()
     */
    public IErrorReporter getErrorReporter()
    {
        return err;
    }

    /* (non-Javadoc)
     * @see descent.building.compiler.IBuildManager#getLaunchConfiguration()
     */
    public ILaunchConfiguration getLaunchConfiguration()
    {
        return req.getLaunchConfig();
    }

    /* (non-Javadoc)
     * @see descent.building.compiler.IBuildManager#getImportPaths()
     */
    public Collection<File> getImportPaths()
    {
        return importPath;
    }
}
