package descent.internal.building.debuild;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.building.IDBuilder;
import descent.building.compiler.BuildException;
import descent.building.compiler.ICompilerInterface;

public class DebuildBuilder implements IDBuilder
{   
    /* package */ static final boolean DEBUG = true;
	
    public String build(ILaunchConfiguration config, ILaunch launch,
            IProgressMonitor pm)
	{
        ErrorReporter err = null;
        
		if(null == pm)
			pm = new NullProgressMonitor();
		
		if(pm.isCanceled())
			return null;
		
		try
		{
			pm.beginTask("Building D application", 100);
			
			// Step 1 : Create the build request & error reporter
			BuildRequest req = new BuildRequest(config);
			err = new ErrorReporter(req.getProject());
            ICompilerInterface compiler = req.getCompilerInterface();
			pm.worked(5); // 5
	        
	        // Step 2: Create the dsss.conf file
			if(pm.isCanceled())
	            return null;
	        generateConfigFile(req, err, compiler, new SubProgressMonitor(pm, 20)); // 25
	        
	        // Step 3: Fork a DSSS process & capture the output
	        if(pm.isCanceled())
	            return null;
	        // TODO
	        
	        return null;
		}
        catch(BuildException e)
        {
            if(null != err)
                err.projectError(e.getMessage());
            if(DEBUG)
                e.printStackTrace();
            return null;
        }
        catch(Exception e)
        {
            if(DEBUG)
                e.printStackTrace();
            if(e instanceof RuntimeException)
                throw (RuntimeException) e;
            else
                throw new RuntimeException(e);
        }
		finally
		{
			pm.done();
		}
	}
    
    private void generateConfigFile(BuildRequest req, ErrorReporter err, ICompilerInterface compiler,
            IProgressMonitor pm)
    {
        ConfigFileGenerator cfgGen = new ConfigFileGenerator(req, err, compiler);
        cfgGen.writeConfigFile(pm);
        System.gc();
    }
}