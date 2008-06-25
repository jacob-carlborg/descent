package descent.internal.building.debuild;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.internal.building.BuilderUtil;
import descent.launching.IVMInstall;
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
/* package */ class BuildRequest
{
	private final ILaunchConfiguration config;
    private final IJavaProject project;
    private final IVMInstall compilerInstall;
    private final ICompilerInterface compilerInterface;
    
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
		    throw new DebuildException("Could not get compiler for project " +
		            project.getElementName());
		}
		compilerInterface = BuilderUtil.getCompilerInterface(compilerInstall);
		
		// Set debug/version settings
		sourceProject = getSourceProject();
		
		debugLevel = getLevel(ATTR_DEBUG_LEVEL, JavaCore.COMPILER_DEBUG_LEVEL);
		versionLevel = getLevel(ATTR_VERSION_LEVEL, JavaCore.COMPILER_VERSION_LEVEL);
		debugIdents = getIdents(ATTR_DEBUG_IDENTS, JavaCore.COMPILER_DEBUG_IDENTIFIERS, false);
		versionIdents = getIdents(ATTR_VERSION_IDENTS, JavaCore.COMPILER_VERSION_IDENTIFIERS, true);
		debugMode = BuilderUtil.getAttribute(config, ATTR_DEBUG_MODE, true);
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
     * Gets the base modules that should be built; dependancies may also need to
     * be built
     */
    public ICompilationUnit[] getModules()
    {
        List<String> handles = BuilderUtil.getAttribute(config, ATTR_MODULES_LIST, 
                BuilderUtil.EMPTY_LIST);
        
        if(handles.isEmpty())
            throw new DebuildException("No target modules defined");
        
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
            throw new DebuildException("None of the modules to be built exist in the project.");
        
        return modules.toArray(new ICompilationUnit[modules.size()]);
    }
	
	public final List<String> getVersionIdents() {
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
    
    //--------------------------------------------------------------------------
    // Private methods
    
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
            throw new DebuildException(String.format("Cannot find project %1$s", project.getElementName()));
        
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
            for(String ident : idents)
                if(BuilderUtil.isPredefinedVersion(ident))
                    idents.remove(ident);
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
    
    //--------------------------------------------------------------------------
    // Private constants
    // PERHAPS these shouldn't be hardcoded
    
    // Ignored modules
	private static final String[] phobosIgnored = new String[]
    {
        "object",
        "crc32",
        "gcc.",
        "gcstats",
        "std.",
    };
	private static final String[] tangoIgnored = new String[]
    {
        "object",
        "gcc.",
    };
}
