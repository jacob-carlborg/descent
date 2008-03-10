package descent.internal.launching.debuild;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import descent.core.IClasspathEntry;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.launching.BuildCancelledException;
import descent.launching.IExecutableTarget;
import descent.launching.compiler.BuildError;
import descent.launching.compiler.BuildResponse;
import descent.launching.compiler.ICompileCommand;
import descent.launching.compiler.ICompilerInterface;
import descent.launching.compiler.IResponseInterpreter;

/**
 * The main engine of the descent remote builder. Given an executable target
 * (info on what type of executable is needed) and a project, performs the
 * build. The publuc interface of this class can be accessed via the
 * {@link #build(IExecutableTarget, IProgressMonitor)} method,
 * which will initiat a build.
 * 
 * @author Robert Fraser
 */
public class DebuildBuilder
{
	/**
	 * Public interface to the debuild builder, which initiates a new build
	 * based on the given executable target. The target
	 * should contain information on what is to be built. Returns the path to
	 * the executable file if one is built (or already exists in the project)
     * or null if the project could not be built.
	 * 
	 * @param target information about the target executable to be built
	 * @param pm     a monitor to track the progress of the build
	 * @return       the path to the executable file,
	 */
	public static String build(IExecutableTarget target, IProgressMonitor pm)
	{
		DebuildBuilder builder = new DebuildBuilder(new BuildRequest(target));
        String executableFilePath = builder.build(pm);
        Assert.isTrue(null != executableFilePath);
        return executableFilePath;
    }
	
    /**
     * Gets the absolute OS path for the given Eclipse path (with portable
     * separarators, etc.).
     * 
     * @param path
     * @return
     */
    public static String getAbsolutePath(IPath path)
    {
        IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
        if(null != res)
            path = res.getLocation();
        
        return path.toPortableString();
    }
    
    /* package */ static final boolean DEBUG = true;
    
	private final BuildRequest req;
	private final ErrorReporter err;
	
    private List<File> importPath;
    private ObjectFile[] objectFiles;
    private List<GroupedCompile> groupedCompiles;
    private CompileOptions opts;
    
	private DebuildBuilder(BuildRequest req)
	{
		this.req = req;
		this.err = new ErrorReporter(req.getProject());
	}
	
	private String build(IProgressMonitor pm)
	{
		if(null == pm)
			pm = new NullProgressMonitor();
		
		if(pm.isCanceled())
			throw new BuildCancelledException();
		
		try
		{   
			pm.beginTask("Building D application", 100);
			
			// Usually, a little work has been done by now. Move the progress bar to keep the
			// user in a pleasent and productive mood
			pm.worked(5); // 5
			
            IJavaProject project = req.getProject();
            
			// First, create the import path from the project properties, etc.
            System.out.println("Creating import path");
			createImportPath();
            pm.worked(10); // 15
            
			if(pm.isCanceled())
				throw new BuildCancelledException();
			
			// Then, recursively collect dependancies for all the object files
            System.out.println("Finding dependancies");
			objectFiles = RecursiveDependancyCollector.getObjectFiles(
					project,
					req.getModules(),
					req.getIgnoreList(),
					new SubProgressMonitor(pm, 25)); // 40
			
			if(pm.isCanceled())
				throw new BuildCancelledException();
			
            // Then, get the compile options we should use and apply them to
            // all the object files
            System.out.println("Getting compile options");
            opts = req.getCompileOptions();
            for(ObjectFile obj : objectFiles)
                obj.setOptions(opts);
            pm.worked(5); // 45
            
            // Create a set of grouped compiles for each compile group we need
            System.out.println("Creating compile groups");
            createCompileGroups();
            pm.worked(5); // 50
            
            
            // Perform each compile operation
            System.out.println("Executing compiler application");
            if(!performCompile(new SubProgressMonitor(pm, 30))) // 80
                return null;
            
			// TODO link
			throw new DebuildException();
		}
        catch(DebuildException e)
        {
            return null;
        }
        catch(Exception e)
        {
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
        }
        catch(JavaModelException e)
        {
            throw new RuntimeException(e);
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
                    importPath.add(new File(getAbsolutePath(entry.getPath())));
                    break;
                case IClasspathEntry.CPE_PROJECT:
                    String projectName = entry.getPath().lastSegment();
                    IJavaProject requiredProject = model.getJavaProject(projectName);
                    if(null != requiredProject && requiredProject.exists())
                        addClasspath(requiredProject, visitedProjects);
                    break;
                default:
                    throw new RuntimeException("Invalid resolved classpath entry type");
            }
        }
    }
    
    private void createCompileGroups()
    {
        // TODO all-at-once support
        groupedCompiles = new ArrayList<GroupedCompile>(objectFiles.length);
        for(ObjectFile obj : objectFiles)
        {
            GroupedCompile gc = new GroupedCompile();
            gc.addObjectFile(obj);
            groupedCompiles.add(gc);
        }
    }
    
    private boolean performCompile(IProgressMonitor pm)
    {
        try
        {
            pm.beginTask("Invoking compiler", groupedCompiles.size() * 10);
            
            boolean wasSuccesful = true;
            ICompilerInterface compilerInterface = req.getCompilerInterface();
            IJavaProject project = req.getProject();
            String workingDirectory = getAbsolutePath(project.getOutputLocation());
            File outputDirectory = new File(workingDirectory);
            
            for(GroupedCompile gc : groupedCompiles)
            {
                ICompileCommand cmd = req.getCompileCommand();
                cmd.setOutputDirectory(outputDirectory);
                
                for(File entry : importPath)
                    cmd.addImportPath(entry);
                pm.worked(2);
                
                for(ObjectFile obj : gc.getObjectFiles())
                    cmd.addFile(obj.getInputFile());
                pm.worked(2);
                
                IResponseInterpreter responseInterpreter = 
                    compilerInterface.createCompileResponseInterpreter();
                /* ExecutionMonitor executor = new ExecutionMonitor(cmd.getCommand(),
                        responseInterpreter,
                        null,
                        workingDirectory);
                executor.run(); */
                System.out.println(cmd.getCommand()); // TODO
                pm.worked(4);
                
                BuildResponse response = responseInterpreter.getResponse();
                for(BuildError error : response.getBuildErrors())
                {
                    err.buildError(error);
                }
                if(!response.wasSuccessful())
                    wasSuccesful = false;
                pm.worked(2);
            }
            return wasSuccesful;
        }
        catch(JavaModelException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            pm.done();
        }
    }
}