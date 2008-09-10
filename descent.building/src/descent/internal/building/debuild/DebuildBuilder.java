package descent.internal.building.debuild;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.core.IClasspathEntry;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.internal.building.BuilderUtil;
import descent.building.IDBuilder;
import descent.building.compiler.BuildException;
import descent.building.compiler.IBuildManager;
import descent.building.compiler.ICompileManager;
import descent.building.compiler.IObjectFile;

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
			pm.worked(5); // 5
	        
	        // Step 2: Find the import paths and put them in the list
	        List<File> importPath = createImportPath(req);
	        pm.worked(5); // 10
	        
	        // Step 3: Make sure any files that need to be rebuilt are removed from the 
	        // temporary working directory
	        checkIncrementalConsistency(req);
	        pm.worked(5); // 15
	        
	        // Step 4: Collect the dependencies that need to be built into object files and
	        // create an internal list of IObjectFiles
	        IObjectFile[] objects = collectObjectFiles(req, new SubProgressMonitor(pm, 30)); // 45
	        
	        // Step 5: Create the compiler-specific compile manager
	        IBuildManager buildMgr = new BuildManager(req, err, importPath, launch);
	        ICompileManager compileMgr = req.getCompilerInterface().getCompileManager(buildMgr);
	        pm.worked(5); // 50
	        
	        // Step 6: Invoke the compiler on the object files -- it's all up to it now
	        compileMgr.compile(objects, new SubProgressMonitor(pm, 50)); // 100
	        
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
    
    private void checkIncrementalConsistency(BuildRequest req)
    {
        IncrementalConsistencyManager icm = new IncrementalConsistencyManager(req);
        icm.checkConsistentState();
    }
    
    private IObjectFile[] collectObjectFiles(BuildRequest req, IProgressMonitor pm)
    {
        // TODO dependency information can be cached... maybe during the internal
        // build step? This would be a less modular but perhaps orders-of-magnitude
        // faster design
        ObjectFileFactory objFactory = new ObjectFileFactory(req);
        RecursiveDependancyCollector collector = new RecursiveDependancyCollector(req, objFactory);
        return collector.getModules(pm);
    }

    //--------------------------------------------------------------------------
    // Import path management
    
    private List<File> createImportPath(BuildRequest req)
	{
        try
        {
            List<File> importPath = new ArrayList<File>();
            addClasspath(importPath, req.getProject(), new HashSet<IJavaProject>());
            /* PERHAPS default import path for things like flute?
            for(String entry : req.getDefaultImportPath())
                importPath.add(new File(entry)); */
            return importPath;
        }
        catch(JavaModelException e)
        {
            throw new BuildException(e.getMessage());
        }
	}
    
    private void addClasspath(List<File> importPath,
            IJavaProject project,
            Set<IJavaProject> visitedProjects)
        throws JavaModelException
    {
        if(visitedProjects.contains(project))
            return;
        visitedProjects.add(project);
        
        IJavaModel model = project.getJavaModel();
        for(IClasspathEntry entry: project.getResolvedClasspath(false))
        {
            // PERHAPS exclusion patterns? should we care?
            // PERHAPS errors for unresolved classpath enteries
            switch(entry.getEntryKind())
            {
                case IClasspathEntry.CPE_SOURCE:
                case IClasspathEntry.CPE_LIBRARY:
                    importPath.add(new File(BuilderUtil.getAbsolutePath(entry.getPath())));
                    break;
                case IClasspathEntry.CPE_PROJECT:
                    String projectName = entry.getPath().lastSegment();
                    IJavaProject requiredProject = model.getJavaProject(projectName);
                    if(null != requiredProject && requiredProject.exists())
                        addClasspath(importPath, requiredProject, visitedProjects);
                    break;
                default:
                    throw new RuntimeException("Unexpected resolved classpath entry: "
                            + entry);
            }
        }
    }
}