package descent.internal.building.debuild;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.internal.building.BuilderUtil;
import descent.launching.IVMInstall;
import descent.building.compiler.BuildException;
import descent.building.compiler.ICompilerInterface;

import static descent.building.IDescentBuilderConstants.*;

/**
 * Wrapper for information about a build request. Exactly one object
 * of this type will exist per DebuildBuilder, and this object should
 * generally simply serve as a wrapper for abstracting getting information
 * that the build needs from the {@link ILaunchConfiguration}.
 *
 * @author Robert Fraser
 */
@SuppressWarnings("unchecked")
/* package */ final class BuildRequest
{
	private final ILaunchConfiguration config;
    private final IJavaProject project;
    private final IVMInstall compilerInstall;
    private final ICompilerInterface compilerInterface;
    private final IFolder outputResource;
    private final String projectBaseLoc;
    
    // For version/debug settings
    private final IJavaProject sourceProject;
    private final Integer versionLevel;
    private final List<String> versionIdents;
    private final Integer debugLevel;
    private final List<String> debugIdents;
    private final boolean debugMode;
	
	public BuildRequest(ILaunchConfiguration config)
	{
		this.config = config;
		project = initializeProject();
		try
		{
		    compilerInstall = BuilderUtil.getVMInstall(project);
		}
		catch(CoreException e)
		{
		    throw new BuildException("Could not get compiler for project " +
		            project.getElementName());
		}
		compilerInterface = BuilderUtil.getCompilerInterface(compilerInstall);
		outputResource = setOutputResource();
		projectBaseLoc = BuilderUtil.getAbsolutePath(project.getPath());
		
		// Set debug/version settings
		sourceProject = getSourceProject();
		debugLevel = getLevel(ATTR_DEBUG_LEVEL, JavaCore.COMPILER_DEBUG_LEVEL);
		versionLevel = getLevel(ATTR_VERSION_LEVEL, JavaCore.COMPILER_VERSION_LEVEL);
		debugIdents = getIdents(ATTR_DEBUG_IDENTS, JavaCore.COMPILER_DEBUG_IDENTIFIERS, false);
		// TODO should predefined versions be removed?
		versionIdents = getIdents(ATTR_VERSION_IDENTS, JavaCore.COMPILER_VERSION_IDENTIFIERS, false);
		debugMode = BuilderUtil.getAttribute(config, ATTR_DEBUG_MODE, true);
	}
	
	/**
	 * Gets the name of the underlying launch configuration
	 */
	public String getName()
	{
	    return config.getName();
	}
	
	/**
     * Gets the Java project being built
     */
    public IJavaProject getProject()
    {
        return project;
    }
    
    /**
     * Gets the compiler interface to use
     */
    public ICompilerInterface getCompilerInterface()
    {
        return compilerInterface;
    }
    
    /**
     * Gets the associated launch configuration. Using the specific methods
     * should be preferred to this for getting information from the config.
     */
    public ILaunchConfiguration getLaunchConfig()
    {
        return config;
    }
    
    /**
     * Gets the base modules that should be built; dependancies may also need to
     * be built
     */
    public ICompilationUnit[] getModules()
    {
        List<String> handles = BuilderUtil.getAttribute(config, ATTR_MODULES_LIST, 
                BuilderUtil.EMPTY_LIST);
        
        if(handles.isEmpty())
            throw new BuildException("No target modules defined");
        
        List<ICompilationUnit> modules = 
            new ArrayList<ICompilationUnit>(handles.size());
        for(String handle : handles)
        {
            IJavaElement element = JavaCore.create(handle);
            if(null != element)
            {
                if(element instanceof IPackageFragment)
                {
                    if(!element.exists())
                    {
                        // TODO mark a warning condition
                        continue;
                    }
                    
                    IPackageFragment pkg = (IPackageFragment) element;
                    try
                    {
                        for(IJavaElement child : pkg.getChildren())
                            if(child instanceof ICompilationUnit && child.exists())
                                modules.add((ICompilationUnit) child);
                    }
                    catch(JavaModelException e)
                    {
                        // TODO mark a warning condition
                    }
                }
                else if(element instanceof ICompilationUnit)
                {
                    modules.add((ICompilationUnit) element);
                }
            }
        }
        
        if(modules.isEmpty())
            throw new BuildException("None of the modules to be built exist in the project.");
        
        return modules.toArray(new ICompilationUnit[modules.size()]);
    }
	
	public final List<String> getVersionIdents()
	{
        return versionIdents;
    }

    public final Integer getDebugLevel()
    {
        return debugLevel;
    }

    public final List<String> getDebugIdents()
    {
        return debugIdents;
    }

    public final Integer getVersionLevel()
    {
        return versionLevel;
    }
    
    public final boolean isDebugMode()
    {
        return debugMode;
    }
    
    public IFolder getOutputResource()
    {
        return outputResource;
    }
    
    public File getOutputLocation()
    {
        return new File(BuilderUtil.getAbsolutePath(outputResource.getFullPath()));
    }
    
    public IPath getProjectBasePath()
    {
        return project.getResource().getLocation();
    }
    
    public String getProjectBaseLocation()
    {
        return projectBaseLoc;
    }
    
    public String getTargetFile()
    {
        return BuilderUtil.getAttribute(config, ATTR_OUTPUT_FILE, "");
    }
    
    public String getAdditionalCompilerArgs()
    {
        return BuilderUtil.getAttribute(config, ATTR_ADDITIONAL_COMPILER_ARGS, "");
    }
    
    public String getAdditionalLinkerArgs()
    {
        return BuilderUtil.getAttribute(config, ATTR_ADDITIONAL_LINKER_ARGS, "");
    }
    
    //--------------------------------------------------------------------------
    // Private methods
    
    private IFolder setOutputResource()
    {
        try
        {
            IPath outputLoc = project.getOutputLocation().addTrailingSeparator().append("__" + getName());
            return project.getCorrespondingResource().getWorkspace().getRoot().getFolder(outputLoc);
        }
        catch(JavaModelException e)
        {
            throw new BuildException(e);
        }
    }
    
    private final IJavaProject initializeProject()
    {
        String projectName = BuilderUtil.getAttribute(config, ATTR_PROJECT_NAME, "");
        IJavaProject project = null;
        if(!"".equals(projectName))
        {
            project = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).
                getJavaProject(projectName);
        }
        
        if(null == project)
            throw new BuildException(String.format("Cannot find project %1$s", projectName));
        
        return project;
    }
    
    private IJavaProject getSourceProject()
    {
        String source = BuilderUtil.getAttribute(config, ATTR_VERSION_SOURCE, SOURCE_ACTIVE_PROJECT);
        if(SOURCE_SELECTED_PROJECT.equals(source))
            return project;
        else if(SOURCE_ACTIVE_PROJECT.equals(source))
            return project; // TODO
        else
            return null;
    }
    
    private Integer getLevel(String configId, String optionsId)
    {
        String levelStr = BuilderUtil.getAttribute(config, configId, "");
        if(null == levelStr || "".equals(levelStr))
        {
            levelStr = getLevelFromProject(optionsId);
        }
        if(null != levelStr && !("".equals(levelStr)))
        {
            try
            {
                return new Integer(levelStr);
            }
            catch(NumberFormatException e) { }
        }
        return null;
    }
    
    private List<String> getIdents(String configId, String optionsId, boolean removePredefined)
    {
        List<String> idents = BuilderUtil.getAttribute(config, configId, BuilderUtil.EMPTY_LIST);
        addIdentifiersFromProject(idents, optionsId);
        
        if(removePredefined)
        {
            Iterator<String> iter = idents.iterator();
            while(iter.hasNext())
            {
                String ident = iter.next();
                if(BuilderUtil.isPredefinedVersion(ident))
                    iter.remove();
            }
        }
        return idents;
    }
    
    private String getLevelFromProject(String optionId)
    {
        if(null == sourceProject)
            return null;
        return sourceProject.getOption(optionId, true);
        
    }
    
    private void addIdentifiersFromProject(List<String> idents, String preference)
    {
        if(null == sourceProject)
            return;
        
        String[] option = sourceProject.getOption(preference, true).split(",");
        csv: for(String val : option)
        {
            val = val.trim();
            if(val.equals(""))
                continue csv;
            for(int i = 0; i < val.length(); i++)
            {
                char c = val.charAt(i);
                if(!(
                    (c >= 'A' && c <= 'Z') ||
                    (c >= 'a' && c <= 'z') ||
                    (c >= '0' && c <= '9') ||
                    (c == '_')
                ))
                    continue csv;
            }
            if(!idents.contains(val))
                idents.add(val);
        }
    }
}
