package descent.internal.building.debuild;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.core.IClasspathEntry;
import descent.core.ICompilationUnit;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.internal.building.BuilderUtil;
import descent.building.IDBuilder;
import descent.building.compiler.BuildException;
import descent.building.compiler.ICompileManager;
import descent.building.compiler.ICompilerInterface;
import descent.building.compiler.IObjectFile;

public class DebuildBuilder implements IDBuilder
{   
    /* package */ static final boolean DEBUG = true;
    
	private BuildRequest req;
	private ErrorReporter err;
	private BuildManager buildMgr;
	private ObjectFileFactory objFactory;
	
    private List<File> importPath;
	
    public String build(ILaunchConfiguration config, ILaunch launch,
            IProgressMonitor pm)
	{   
		if(null == pm)
			pm = new NullProgressMonitor();
		
		if(pm.isCanceled())
			return null;
		
		try
		{   
		    // Create the build request & error reporter
			pm.beginTask("Building D application", 100);
			req = new BuildRequest(config);
	        err = new ErrorReporter(req.getProject());
	        buildMgr = new BuildManager();
	        objFactory = new ObjectFileFactory(req);
			pm.worked(5); // 5
			
			// Check whether this build is even possible
			// NEXTVERSION support internal dependency analysis
			ICompilerInterface compilerInterface = req.getCompilerInterface();
            if(null == compilerInterface || 
                    !compilerInterface.supportsInternalDependancyAnalysis())
                throw new BuildException("This compiler type is not supported in " +
                		"this version of Descent.");
            
			// If the launch configuration has changed, clear the output folder
			// to do a full rebuild
			IncrementalConsistencyManager consistencyMgr = 
			    new IncrementalConsistencyManager(req);
			consistencyMgr.checkConsistentState();
			pm.worked(5); // 10
			
			// Create the import path 
			createImportPath();
            pm.worked(5); // 15
            
			if(pm.isCanceled())
				return null;
			
			doCompile(new SubProgressMonitor(pm, 60)); // 85
			if(pm.isCanceled())
                return null;
			
			return doLink(new SubProgressMonitor(pm, 15)); // 100
		}
        catch(BuildException e)
        {
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

    //--------------------------------------------------------------------------
    // Import path management
    
    private void createImportPath()
	{
        try
        {
            importPath = new ArrayList<File>();
            addClasspath(req.getProject(), new HashSet<IJavaProject>());
            /* PERHAPS default import path for things like flute?
            for(String entry : req.getDefaultImportPath())
                importPath.add(new File(entry)); */
        }
        catch(JavaModelException e)
        {
            throw new BuildException(e.getMessage());
        }
	}
    
    private void addClasspath(IJavaProject project,
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
                        addClasspath(requiredProject, visitedProjects);
                    break;
                default:
                    throw new RuntimeException("Unexpected resolved classpath entry: "
                            + entry);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // Compile management
    
    private void doCompile(IProgressMonitor pm)
    {
        try
        {
            // TODO what to do about progress monitoring?
            pm.beginTask("Compiling object files", 1000);
            
            ICompileManager compileMgr = req.getCompilerInterface().
                    getCompileManager(buildMgr);
            Set<IObjectFile> pending = new HashSet<IObjectFile>();
            Set<IObjectFile> completed = new HashSet<IObjectFile>();
            
            // Add the initial/seed objects
            for(ICompilationUnit cu : req.getModules())
            {
                pending.add(objFactory.create(cu.getFullyQualifiedName()));
                if(pm.isCanceled())
                    return;
            }
            
            while(!pending.isEmpty())
            {
                // Remove any object files that should not be built
                Iterator<IObjectFile> iter = pending.iterator();
                objects: while(iter.hasNext())
                {
                    IObjectFile obj = iter.next();
                    
                    // Check if it's already been built during this run
                    if(completed.contains(obj))
                    {
                        iter.remove();
                        continue objects;
                    }
                    
                    // Check if the file has already been built during another
                    // run and is still up-to-date
                    if((obj.getInputFile().exists()) &&
                       (obj.getOutputFile().exists()) &&
                       (obj.getOutputFile().lastModified() >= 
                           obj.getInputFile().lastModified()))
                    {
                        completed.add(obj);
                        iter.remove();
                        continue objects;
                    }
                }
                
                if(pending.isEmpty())
                    break;
                if(pm.isCanceled())
                    return;
                
                // Compile the rest
                String[] dependancies = compileMgr.compile(
                        pending.toArray(new IObjectFile[pending.size()]),
                        new SubProgressMonitor(pm, 10));
                if(pm.isCanceled())
                    return;
                
                // Flush the pending list into the completed list and put any
                // dependencies into the pending list
                completed.addAll(pending);
                pending.clear();
                for(String moduleName : dependancies)
                {
                    IObjectFile obj = objFactory.create(moduleName);
                    if(null != obj)
                        pending.add(obj);
                    if(pm.isCanceled())
                        return;
                }
            }
        }
        finally
        {
            pm.done();
        }
    }
    
    //--------------------------------------------------------------------------
    // Link management
    
    private String doLink(IProgressMonitor pm)
    {
        try
        {
            pm.beginTask("Linking output file", 100);
            
            return null;
        }
        finally
        {
            pm.done();
        }
    }
}