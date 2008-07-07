package descent.internal.building.debuild;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import descent.core.IClasspathEntry;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.internal.building.BuilderUtil;
import descent.building.IDBuilder;

public class DebuildBuilder implements IDBuilder
{   
    /* package */ static final boolean DEBUG = true;
    
    /**
     * Note: don't use this as the main null progress monitor, since its 
     * canceleld state is not garunteed.
     */
    private static final IProgressMonitor NO_MONITOR = new NullProgressMonitor();
    
	private BuildRequest req;
	private ErrorReporter err;
    private List<File> importPath;
	
    public String build(ILaunchConfiguration config, IProgressMonitor pm)
            throws CoreException
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
			pm.worked(5); // 5
            
			// If the launch configuration has changed, clear the output folder
			// to do a full rebuild
			managePrecomiledResources();
			pm.worked(5); // 10
			
			// Create the import path 
			createImportPath();
            pm.worked(5); // 15
            
			if(pm.isCanceled())
				return null;
			
			// TODO
			return null;
		}
        catch(DebuildException e)
        {
            err.projectError(e.getMessage());
            throw e; // TODO remove
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
	
    /**
     * If the launch configuration has changed, all pre-compiled resources need to
     * be removed and a full rebuild done (PERHAPS a more selective mechanism -- if
     * the user just changed the output file, for example, no rebuild is needed).
     * This method detects if the current launch configuration is different than the
     * existing launch configuration and if so deletes everything in the folder.
     * 
     * @param config
     */
	private void managePrecomiledResources()
    {
	    // TODO this doesn't work (it adds a new launch). FInd out if there's a way
	    // not to, alternatively a new serialization mechanism needs to be created
	    // (or maybe just somehow store the age/revision of the last change).
        try
        {
            // Check if the folder exists
            IFolder folder = req.getOutputResource();
            if(!folder.exists())
            {
                folder.create(true, true, NO_MONITOR);
                createConfigFile();
                return;
            }

            folder.refreshLocal(IResource.DEPTH_INFINITE, NO_MONITOR);
            IResource launchConfigFile = folder.findMember(getLaunchConfigFilename());
            if(null == launchConfigFile || !(launchConfigFile instanceof IFile))
            {
                System.out.println("File doesn't exist!");
                clearOutputFolder();
                createConfigFile();
                return;
            }
            
            ILaunchConfiguration launchConfig = DebugPlugin.getDefault().
                    getLaunchManager().getLaunchConfiguration((IFile) launchConfigFile);
            if(!BuilderUtil.launchConfigsEqual(launchConfig, req.getLaunchConfig()))
            {
                System.out.println("Not equal!");
                clearOutputFolder();
                createConfigFile();
                return;
            }
            
            // If we get here, we can safely use any object files already
            // generated for incremental compilation.
            System.out.println("Contents saved!!!");
        }
        catch(CoreException e)
        {
            throw new DebuildException(String.format(
                    "Error preparing output folder: %1$s", e.getMessage()));
        }
        
    }
	
	private String getLaunchConfigFilename()
    {
        return req.getLaunchConfig().getName().concat(".").
                concat(ILaunchConfiguration.LAUNCH_CONFIGURATION_FILE_EXTENSION);
    }

    private void createConfigFile() throws CoreException
	{
	    ILaunchConfiguration config = req.getLaunchConfig();
	    ILaunchConfigurationWorkingCopy copy = config.copy(config.getName());
	    copy.setContainer(req.getOutputResource());
	    copy.doSave();
	}
	
	private void clearOutputFolder() throws CoreException
	{
	    System.out.println("Folder cleared!!!");
	    
	    IFolder outputFolder = req.getOutputResource();
	    IResource[] members = outputFolder.members();
	    for(IResource file : members)
	    {
	        try
	        {
	            file.delete(true, NO_MONITOR);
	        }
	        catch(CoreException e)
	        {
	            throw new DebuildException(String.format(
	                    "Error deleting resource %1$s: %2$s", 
	                    file.getFullPath().toString(), e.getMessage()));
	        }
	    }
	}

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
            throw new DebuildException(e.getMessage());
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
}