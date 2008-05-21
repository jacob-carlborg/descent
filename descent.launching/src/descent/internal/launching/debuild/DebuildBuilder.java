package descent.internal.launching.debuild;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.core.IClasspathEntry;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.launching.IDBuilder;
import descent.launching.compiler.BuildError;
import descent.launching.compiler.BuildResponse;
import descent.launching.compiler.ICompileCommand;
import descent.launching.compiler.ICompilerInterface;
import descent.launching.compiler.ILinkCommand;
import descent.launching.compiler.IResponseInterpreter;

// TODO recomment
public class DebuildBuilder implements IDBuilder
{    
    /* package */ static final boolean DEBUG = true;
    /* package */ static final String EXECUTABLE_FILE_PREFIX = "-";
    
	private BuildRequest req;
	private ErrorReporter err;
	
    private List<File> importPath;
    private ObjectFile[] objectFiles;
    private List<GroupedCompile> groupedCompiles;
    private CompileOptions opts;
	
    public String build(ILaunchConfiguration config, IProgressMonitor pm)
            throws CoreException
	{
        // TODO remove
        if(true)
        {
            System.out.println("We here, baby!");
            return null;
        }
        
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
			
			// Then, recursively collect dependancies for all the object files
			objectFiles = RecursiveDependancyCollector.getObjectFiles(
					project,
					req.getModules(),
					req.getIgnoreList(),
					new SubProgressMonitor(pm, 25)); // 40
			
			if(pm.isCanceled())
				return null;
			
            // Then, get the compile options we should use and apply them to
            // all the object files
            opts = req.getCompileOptions();
            for(ObjectFile obj : objectFiles)
                obj.setOptions(opts);
            pm.worked(5); // 45
            
            if(pm.isCanceled())
                return null;
            
            // Create a set of grouped compiles for each compile group we need
            createCompileGroups();
            pm.worked(5); // 50
            
            if(pm.isCanceled())
                return null;
            
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
                    return null;
                
                // Get & setup a new compile command
                ICompileCommand cmd = req.getCompileCommand();
                cmd.setOutputDirectory(outputDirectory);
                opts.prepareCompileCommand(cmd);
                
                // Add the import path to the compile command
                cmd.setImportPaths(importPath);
                pm.worked(2); // 2
                
                // Add the input files to the compile command
                List<File> inputFiles = new ArrayList<File>(gc.size());
                for(ObjectFile obj : gc)
                    inputFiles.add(obj.getInputFile());
                cmd.setFiles(inputFiles);
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
                return null;
            
            // Create the linker command
            ILinkCommand cmd = req.getLinkCommand();
            File outputFile = new File(workingDirectory + "/" + getExecutableName());
            cmd.setOutputFilename(outputFile);
            List<File> inputFiles = new ArrayList<File>(objectFiles.length);
            for(ObjectFile obj : objectFiles)
                inputFiles.add(obj.getOutputFile());
            cmd.setFiles(inputFiles);
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