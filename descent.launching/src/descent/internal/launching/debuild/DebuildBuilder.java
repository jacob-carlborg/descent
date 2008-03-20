package descent.internal.launching.debuild;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
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
import descent.launching.compiler.ILinkCommand;
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
	 * @return       the path to the executable file or null if one could not
     *               be built due to an error
	 */
	public static String build(IExecutableTarget target, IProgressMonitor pm)
	{
		DebuildBuilder builder = new DebuildBuilder(new BuildRequest(target));
        return builder.build(pm);
    }
    
    /* package */ static final boolean DEBUG = true;
    
    /* package */ static final String EXECUTABLE_FILE_PREFIX = "-";
    
    //--------------------------------------------------------------------------
    
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
			createImportPath();
            pm.worked(10); // 15
            
			if(pm.isCanceled())
				throw new BuildCancelledException();
			
			// Then, recursively collect dependancies for all the object files
			objectFiles = RecursiveDependancyCollector.getObjectFiles(
					project,
					req.getModules(),
					req.getIgnoreList(),
					new SubProgressMonitor(pm, 25)); // 40
			
			if(pm.isCanceled())
				throw new BuildCancelledException();
			
            // Then, get the compile options we should use and apply them to
            // all the object files
            opts = req.getCompileOptions();
            for(ObjectFile obj : objectFiles)
                obj.setOptions(opts);
            pm.worked(5); // 45
            
            if(pm.isCanceled())
                throw new BuildCancelledException();
            
            // Create a set of grouped compiles for each compile group we need
            createCompileGroups();
            pm.worked(5); // 50
            
            if(pm.isCanceled())
                throw new BuildCancelledException();
            
            // Perform each compile operation
            return runCompile(new SubProgressMonitor(pm, 50)); // 100
		}
        catch(DebuildException e)
        {
            err.projectError(e.getMessage());
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
            for(String entry : req.getDefaultImportPath())
                importPath.add(new File(entry));
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
                    importPath.add(new File(Util.getAbsolutePath(entry.getPath())));
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
    
    private void createCompileGroups()
    {
        // TODO all-at-once support
        groupedCompiles = new ArrayList<GroupedCompile>(objectFiles.length);
        for(ObjectFile obj : objectFiles)
        {
            if(obj.shouldBuild())
                groupedCompiles.add(new SingleFileCompile(obj));
        }
    }
    
    private String runCompile(IProgressMonitor pm)
    {
        try
        {
            pm.beginTask("Invoking compiler", (groupedCompiles.size() * 20) + 20);
            
            boolean wasSuccesful = true;
            ICompilerInterface compilerInterface = req.getCompilerInterface();
            IJavaProject project = req.getProject();
            String workingDirectory = Util.getAbsolutePath(project.getOutputLocation());
            File outputDirectory = new File(workingDirectory);
            
            // Compile the output files
            for(GroupedCompile gc : groupedCompiles)
            {
                if(pm.isCanceled())
                    throw new BuildCancelledException();
                
                // Get & setup a new compile command
                ICompileCommand cmd = req.getCompileCommand();
                cmd.setOutputDirectory(outputDirectory);
                opts.prepareCompileCommand(cmd);
                
                // Add the import path to the compile command
                for(File entry : importPath)
                    cmd.addImportPath(entry);
                pm.worked(2); // 2
                
                // Add the input files to the compile command
                for(ObjectFile obj : gc)
                    cmd.addFile(obj.getInputFile());
                pm.worked(2); // 4
                
                // Execute the application
                IResponseInterpreter responseInterpreter = 
                    compilerInterface.createCompileResponseInterpreter();
                ExecutionMonitor executor = new ExecutionMonitor(cmd.getCommand(),
                        responseInterpreter,
                        null,
                        workingDirectory);
                executor.run();
                pm.worked(12); // 16
                
                // Add error markers if problems were found
                BuildResponse response = responseInterpreter.getResponse();
                for(BuildError error : response.getBuildErrors())
                    err.buildError(error);
                if(!response.wasSuccessful())
                    wasSuccesful = false;
                pm.worked(2); // 18
                
                // Rename the output files
                if(response.wasSuccessful())
                {
                    for(ObjectFile obj : gc)
                        obj.renameOutputFile();
                }
                pm.worked(2); // 20
            }
            
            // If there were compile errors, return here
            if(!wasSuccesful)
                return null;
            
            if(pm.isCanceled())
                throw new BuildCancelledException();
            
            // Create the linker command
            ILinkCommand cmd = req.getLinkCommand();
            File outputFile = new File(workingDirectory + "/" + getExecutableName());
            cmd.setOutputFilename(outputFile);
            for(ObjectFile obj : objectFiles)
                cmd.addFile(obj.getOutputFile());
            // TODO binary libraries
            pm.worked(2); // 2
            
            // Run the linker application proper
            IResponseInterpreter responseInterpreter = 
                compilerInterface.createLinkResponseInterpreter();
            ExecutionMonitor executor = new ExecutionMonitor(cmd.getCommand(),
                    responseInterpreter,
                    null,
                    workingDirectory);
            executor.run();
            pm.worked(16); // 18
            
            // Check for errors
            BuildResponse response = responseInterpreter.getResponse();
            for(BuildError error : response.getBuildErrors())
                err.buildError(error);
            if(!response.wasSuccessful())
                wasSuccesful = false;
            pm.worked(2); // 20
            
            return wasSuccesful && outputFile.exists() ? 
                    outputFile.getAbsolutePath() :
                    null;
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
    
    private String getExecutableName()
    {
        return EXECUTABLE_FILE_PREFIX +
            System.currentTimeMillis() +
            (Util.isWindows() ?
                ".exe" :
                "");
    }
}