package descent.internal.building.debuild;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

import descent.building.compiler.BuildException;
import descent.building.compiler.ICompilerInterface;
import descent.core.IClasspathEntry;
import descent.core.ICompilationUnit;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.internal.building.BuilderUtil;

/* package */ final class ConfigFileGenerator
{
    private static final String CONFIG_FILENAME = "dsss.conf";
    
    private static final Integer ZERO = new Integer(0);
    
    private final BuildRequest req;
    private final ErrorReporter err;
    private final ICompilerInterface compiler;
    
    public ConfigFileGenerator(BuildRequest req, ErrorReporter err, ICompilerInterface compiler)
    {
        this.req = req;
        this.err = err;
        this.compiler = compiler;
    }
    
    //--------------------------------------------------------------------------
    // File I/O
    public void writeConfigFile(IProgressMonitor pm)
    {
        try
        {
            pm.beginTask("Generating DSSS configuration file", 100);
            
            // Create the writer
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            PrintWriter out = new PrintWriter(new BufferedOutputStream(bytes));
            
            // Do the actual work
            generateConfigFile(out, pm); // 90
            
            // Dump the stream to a file
            out.flush();
            createFile(new ByteArrayInputStream(bytes.toByteArray()));
            pm.worked(10); // 100
        }
        finally
        {
            pm.done();
        }
    }

    private void createFile(InputStream source)
    {
        try
        {
            IFolder folder = req.getOutputResource();
            if(!folder.exists())
                folder.create(true, true, BuilderUtil.NO_MONITOR);
            IFile configFile = folder.getFile(CONFIG_FILENAME);
            if(configFile.exists())
                configFile.delete(true, BuilderUtil.NO_MONITOR);
            configFile.create(source, true, BuilderUtil.NO_MONITOR);
        }
        catch(Exception e)
        {
            throw new BuildException(String.format("Error creating dsss.conf file: %1$s", e.getLocalizedMessage()));
        }
    }
    
    //--------------------------------------------------------------------------
    // Content generation
    private void generateConfigFile(PrintWriter out, IProgressMonitor pm)
    {
        try
        {
            out.print("name=");
            out.println(req.getName());
            out.println();
            
            // TODO this is just temporary stuff
            ICompilationUnit module = req.getModules()[0];
            IResource res = module.getUnderlyingResource();
            String path = BuilderUtil.getAbsolutePath(res.getLocation());
            out.print("[");
            out.print(path);
            out.println("]");
            out.println("type=binary");
            
            out.print("target=");
            out.println(req.getTargetFile());
            
            writeBuildflags(out);
            
            pm.worked(90); // TODO
        }
        catch(Exception e)
        {
            // TODO fix
            throw (e instanceof RuntimeException) ? ((RuntimeException) e) : new RuntimeException(e);
        }
    }
    
    private void writeBuildflags(PrintWriter out)
    {
        // Write the buildflags=
        out.print("buildflags=");
        
        // Write the version & debug identifiers/level
        writeIdents(out, req.getVersionIdents(), "-version=", false);
        writeLevel(out, req.getVersionLevel(), "-version=");
        writeIdents(out, req.getDebugIdents(), "-debug=", true);
        writeLevel(out, req.getDebugLevel(), "-debug=");
        
        // Add include paths for other projects
        for(File importPath : getImportPath())
        {
            out.print("-I");
            out.print(importPath.getPath());
            out.print(" ");
        }
        
        // TODO handle the compiler-specific stuff
        
        // Write the additional user arguments
        out.print(req.getAdditionalCompilerArgs());
        out.print(req.getAdditionalLinkerArgs());
    }
    
    private void writeIdents(PrintWriter out, List<String> idents,
            String prefix, boolean allowPredefined)
    {
        for(String ident : idents)
        {
            if(allowPredefined || !BuilderUtil.isPredefinedVersion(ident))
            {
                out.print(prefix);
                out.print(ident);
                out.print(" ");
            }
        }
    }
    
    private void writeLevel(PrintWriter out, Integer level, String prefix)
    {
        if(null != level && !level.equals(ZERO))
        {
            out.print(prefix);
            out.print(level);
            out.print(" ");
        }
    }
    
    //--------------------------------------------------------------------------
    // Import path management
    private List<File> getImportPath()
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
                    throw new RuntimeException(String.format("Unexpected resolved classpath entry: %1$0", entry));
            }
        }
    }
}
