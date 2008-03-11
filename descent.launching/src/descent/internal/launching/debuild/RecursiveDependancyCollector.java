package descent.internal.launching.debuild;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.ICompilationUnit;
import descent.core.IImportDeclaration;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.IParent;
import descent.core.JavaModelException;
import descent.launching.BuildCancelledException;

/**
 * Class that can recurse through dependancies to generate a list of all files
 * that need to be compiled in a module. This class mainly exists to abstract
 * the actual source analysis from the builder. The only method in this class
 * that should eb called externally is 
 * {@link #getObjectFiles(IJavaProject, ICompilationUnit[], IProgressMonitor)},
 * which will get the actual object files necessary using an instance of this
 * class.
 * 
 * @author Robert Fraser
 */
/* package */ class RecursiveDependancyCollector
{
	/**
	 * Gets new {@link ObjectFile}s for all the compilation units given and any
	 * dependancies they may have.
	 * 
	 * @param project  the project the {@link ObjectFile}s should belong to
	 * @param modules  the initial compilation units to search from
	 * @param toIgnore a list of modules to ignore (not build). Entire packages
	 *                 can be ignored by ending the package name with a "."
	 * @param pm       the progress monitor
	 * @return         the object files (and dependancies) taht must be built
	 *                 for this project
	 */
	public static ObjectFile[] getObjectFiles(
			IJavaProject project,
			String[] modules,
			String[] toIgnore,
			IProgressMonitor pm)
	{
		RecursiveDependancyCollector collector = new RecursiveDependancyCollector
			(project, toIgnore);
		return collector.collect(modules, pm);
	}
	
	private final String[] ignoredModules;
	private final Map<String, ObjectFile> objectFiles = 
        new HashMap<String, ObjectFile>();
	private final IJavaProject project;
    
	// Since findModule() is called quite a bit, creating
    // a new hash set every time sounds like a horrible thrashing of the GC.
    // Instead, we'll just keep clearing out these set. They can't be static
    // to maintain the threadsafe nature of the builder as a whole, however
    // (since multiple dependancy collectors may exist at the same time)
    private final Set<IJavaProject> visitedProjectAccumulator = new HashSet<IJavaProject>();
	
	// Shouldn't be constructed directly, use the getObjectFiles method instead
	private RecursiveDependancyCollector(IJavaProject project, String[] ignoredModules)
	{
		this.project = project;
		this.ignoredModules = ignoredModules;
	}
	
	private ObjectFile[] collect(String[] modules, IProgressMonitor pm)
	{
		try
		{
			pm.beginTask("Collecting dependencies", modules.length * 10);
			
			for(String moduleName : modules)
			{
				// Since this can take a long time, checking every so often for cancellation
				// is good!
				if(pm.isCanceled())
					throw new BuildCancelledException();
				
				collectRecursive(moduleName);
				pm.worked(10);
			}
			
			ObjectFile[] arr = new ObjectFile[objectFiles.size()];
			int i = 0;
			for(Entry<String, ObjectFile> entry : objectFiles.entrySet())
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
		for(String ignore : ignoredModules)
			if(moduleName.startsWith(ignore))
				return;
		
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
	
	private ObjectFile createObjectFile(ICompilationUnit cu, 
            boolean isLibraryFile)
	{
		return new ObjectFile(project,
                new File(DebuildBuilder.getAbsolutePath(cu.getPath())),
                cu.getFullyQualifiedName(),
				isLibraryFile);
	}
    
    private ICompilationUnit findModule(String moduleName, boolean[] isLibraryFile)
        throws JavaModelException
    {
        visitedProjectAccumulator.clear();
        return findModuleInProject(project,
                moduleName,
                isLibraryFile);
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
        String modulePart = String.format("%1$s.d", index > 0 ? 
                moduleName.substring(index + 1) : moduleName);
        
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
        
        // fix for bug 87492: visit required projects explicitly to also find not exported types
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
        findImportsInElement(module, imports);
        return imports;
    }
    
    private void findImportsInElement(IParent element, List<String> imports)
        throws JavaModelException
    {
        // TODO
        // Temporary code, delete when the import containers thing is gone
        for(IImportDeclaration importDecl : ((ICompilationUnit) element).getImports())
            imports.add(importDecl.getElementName());
    }
}
