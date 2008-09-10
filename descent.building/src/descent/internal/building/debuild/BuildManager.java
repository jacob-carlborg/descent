package descent.internal.building.debuild;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.Future;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.building.compiler.IBuildManager;
import descent.building.compiler.IErrorReporter;
import descent.building.compiler.IExecutionCallback;
import descent.building.compiler.IExecutionMonitor;
import descent.building.compiler.IResponseInterpreter;

/* package */ final class BuildManager implements IBuildManager
{
    private final BuildRequest req;
    private final ErrorReporter err;
    private final Collection<File> importPath;
    private final ExecutionMonitor executionMonitor;
    
    public BuildManager(BuildRequest req, ErrorReporter err, Collection<File> importPath, ILaunch launch)
    {
        this.req = req;
        this.err = err;
        this.importPath = importPath;
        
        this.executionMonitor = new ExecutionMonitor(launch, req.getOutputLocation().toString(), true); // TODO this isn't always true
    }
    
    /* (non-Javadoc)
     * @see descent.building.compiler.IBuildManager#getExecutionMonitor()
     */
    public IExecutionMonitor getExecutionMonitor()
    {
        return executionMonitor;
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
