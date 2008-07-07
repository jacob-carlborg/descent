package descent.internal.building.debuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.building.compiler.IObjectFile;
import descent.core.ICompilationUnit;
import descent.core.IConditional;
import descent.core.IImportContainer;
import descent.core.IImportDeclaration;
import descent.core.IJavaElement;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaModelException;

/**
 * Class that can recurse through dependencies to generate a list of all files
 * that need to be compiled in a module. This class mainly exists to abstract
 * the actual source analysis from the builder. The only method in this class
 * that should be called externally is 
 * {@link #getObjectFiles(IJavaProject, ICompilationUnit[], IProgressMonitor)},
 * which will get the actual object files necessary using an instance of this
 * class.
 * 
 * @author Robert Fraser
 */
/* package */ class RecursiveDependancyCollector
{	
	/**
	 * Gets new {@link IObjectFile}s for all the compilation units given and any
	 * dependencies they may have.
	 * 
	 * @param modules  the initial compilation units to search from
	 * @param toIgnore a list of modules to ignore (not build). Entire packages
	 *                 can be ignored by ending the package name with a "."
	 * @param pm       the progress monitor
	 * @return         the object files (and dependancies) taht must be built
	 *                 for this project
	 */
	public static IObjectFile[] getObjectFiles(
	        BuildRequest req,
			ObjectFileFactory factory,
			String[] modules,
			IProgressMonitor pm)
	{
		RecursiveDependancyCollector collector = new RecursiveDependancyCollector
			(req, factory);
		return collector.collect(modules, pm);
	}
	
	private final BuildRequest req;
	private final ObjectFileFactory factory;
	
	private final Map<String, IObjectFile> objectFiles = 
        new HashMap<String, IObjectFile>();
    
	// Since findModule() is called quite a bit, creating
    // a new hash set every time sounds like a horrible thrashing of the GC.
    // Instead, we'll just keep clearing out these set. They can't be static
    // to maintain the threadsafe nature of the builder as a whole, however
    // (since multiple dependancy collectors may exist at the same time)
    private final Set<IJavaProject> visitedProjectAccumulator = new HashSet<IJavaProject>();
	
	// Shouldn't be constructed directly, use the getObjectFiles method instead
	private RecursiveDependancyCollector(BuildRequest req, ObjectFileFactory factory)
	{
		this.factory = factory;
		this.req = req;
	}
	
	private IObjectFile[] collect(String[] modules, IProgressMonitor pm)
	{
		try
		{
			pm.beginTask("Collecting dependencies", modules.length * 10);
			
			for(String moduleName : modules)
			{
				// Since this can take a long time, checking every so often for cancellation
				// is good!
				if(pm.isCanceled())
					return null;
				
				collectRecursive(moduleName);
				pm.worked(10);
			}
			
			IObjectFile[] arr = new IObjectFile[objectFiles.size()];
			int i = 0;
			for(Entry<String, IObjectFile> entry : objectFiles.entrySet())
			{
				arr[i] = entry.getValue();
				i++;
			}
			return arr;
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
	
	private void collectRecursive(String moduleName)
		throws JavaModelException
    {	
		// Check if we've already traversed this module
		if(objectFiles.containsKey(moduleName))
			return;
		
		// Check if it's a nodule that should be ignored
		/* TODO for(String ignore : req.getIgnoredModules())
			if(moduleName.startsWith(ignore))
				return; */
		
        // Find the module
        // Note: pass an array of one boolean to get pointer semantics
        boolean[] isLibraryFile = new boolean[1];
        ICompilationUnit module = findModule(moduleName, isLibraryFile);
		if(null == module || !module.exists())
			throw new RuntimeException("Could not find module " + moduleName);
		
        // Add the new object file to the set
		objectFiles.put(moduleName, createObjectFile(module, isLibraryFile[0]));
        
        // Recurse through imports
		for(String importedModule : getImports(module))
			collectRecursive(importedModule);
	}
	
	private IObjectFile createObjectFile(ICompilationUnit cu, 
            boolean isLibraryFile)
	{
		// TODO
		return null;
	}
    
    private ICompilationUnit findModule(String moduleName, boolean[] isLibraryFile)
        throws JavaModelException
    {
        visitedProjectAccumulator.clear();
        return findModuleInProject(req.getProject(), moduleName, isLibraryFile);
    }
    
    private ICompilationUnit findModuleInProject(
            IJavaProject project,
            String moduleName,
            boolean[] isLibraryFile)
        throws JavaModelException
    {
        if (visitedProjectAccumulator.contains(project))
            return null;
        
        int index = moduleName.lastIndexOf('.');
        String packagePart = index > 0 ? moduleName.substring(0, index) : "";
        String modulePart = (index > 0 ? moduleName.substring(index + 1) : 
        	moduleName) + ".d";
        
        ICompilationUnit module = null;
        boolean libraryFile = false;
        for(IPackageFragmentRoot root : project.getPackageFragmentRoots())
        {
            IPackageFragment pkg = root.getPackageFragment(packagePart);
            if (pkg.exists())
            {
                // Check if it's in the package...
                module = pkg.getCompilationUnit(modulePart);
                
                // If not, check if it's a class file
                if (!module.exists())
                {
                    module = pkg.getClassFile(modulePart);
                    libraryFile = true;
                }
                
                if(module.exists())
                {
                    break;
                }
            }
        }
        
        if(null != module && module.exists())
        {
            // Note: existance must be tested since an ICompilationUnit may
            // be returned for non-existant modules
            isLibraryFile[0] = libraryFile;
            return module;
        }
        
        visitedProjectAccumulator.add(project);
        IJavaModel javaModel= project.getJavaModel();
        String[] requiredProjectNames= project.getRequiredProjectNames();
        for (int i= 0; i < requiredProjectNames.length; i++)
        {
            IJavaProject requiredProject= javaModel.getJavaProject(requiredProjectNames[i]);
            if (requiredProject.exists())
            {
                module = findModuleInProject(requiredProject, moduleName, isLibraryFile);
                if (module != null)
                    return module;
            }
        }
        return null;
    }
    
    private List<String> getImports(ICompilationUnit module) 
        throws JavaModelException
    {
        List<String> imports = new ArrayList<String>();
        findImportsInElements(module.getChildren(), imports);
        return imports;
    }
    
    private void findImportsInElements(IJavaElement[] elements, List<String> imports)
    	throws JavaModelException
    {
    	for(IJavaElement element : elements)
        {
            if(element instanceof IImportContainer)
            {
                findImportsInElements(((IImportContainer) element).getChildren(),
                		imports);
                continue;
            }
            
            if(element instanceof IImportDeclaration)
            {
                IImportDeclaration importDecl = (IImportDeclaration) element;
                imports.add(importDecl.getElementName());
                continue;
            }
            
            if(element instanceof IConditional)
            {
                IConditional cond = (IConditional) element;
                findImportsInElements((isActive(cond) ? cond.getThenChildren() :
                	cond.getElseChildren()), imports);
                continue;
            }
        }
    }
    
    private boolean isActive(IConditional cond)
    {
    	System.out.println("Conditional with displayString: " + cond.getElementName());
    	// TODO check if it's active
    	return true;
    }
}
