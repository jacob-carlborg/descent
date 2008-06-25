package descent.internal.building.debuild;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.core.IClasspathEntry;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.internal.building.BuilderUtil;
import descent.building.IDBuilder;

// TODO recomment
public class DebuildBuilder implements IDBuilder
{   
    /* package */ static final boolean DEBUG = true;
    /* package */ static final String EXECUTABLE_FILE_PREFIX = "-";
    
	private BuildRequest req;
	private ErrorReporter err;
    private List<File> importPath;
	
    public String build(ILaunchConfiguration config, IProgressMonitor pm)
            throws CoreException
	{
        // TODO remove
        if(true)
            return null;
        
		if(null == pm)
			pm = new NullProgressMonitor();
		
		if(pm.isCanceled())
			return null;
		
		try
		{   
			pm.beginTask("Building D application", 100);
			
			req = new BuildRequest(config);
	        err = new ErrorReporter(req.getProject());
			pm.worked(5); // 5
			
            IJavaProject project = req.getProject();
            
			// First, create the import path from the project properties, etc.
			createImportPath();
            pm.worked(10); // 15
            
			if(pm.isCanceled())
				return null;
			
			// TODO
			return null;
		}
        catch(DebuildException e)
        {
            err.projectError(e.getMessage());
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