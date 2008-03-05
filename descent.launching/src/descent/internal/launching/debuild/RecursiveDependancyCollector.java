package descent.internal.launching.debuild;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.ICompilationUnit;
import descent.core.IImportDeclaration;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaModelException;
import descent.launching.BuildProcessor.BuildCancelledException;
import descent.launching.BuildProcessor.BuildFailedException;

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
public class RecursiveDependancyCollector
{
	/**
	 * Gets new {@link ObjectFile}s for all the compilation units given and any
	 * dependancies they may have.
	 * 
	 * @param project the project the {@link ObjectFile}s should belong to
	 * @param modules the initial compilation units to search from
	 * @param pm      the progress monitor
	 * @return        the object files (and dependancies) taht must be built for this project
	 */
	public static ObjectFile[] getObjectFiles(
			IJavaProject project,
			String[] modules,
			IProgressMonitor pm)
	{
		RecursiveDependancyCollector collector = new RecursiveDependancyCollector(project);
		return collector.collect(modules, pm);
	}
	
	private static final String[] ignoredModules;
	
	private final Map<String, ObjectFile> objectFiles = new HashMap<String, ObjectFile>();
	private final IJavaProject project;
	
	// Shouldn't be constructed directly, use the getObjectFiles method instead
	private RecursiveDependancyCollector(IJavaProject project)
	{
		this.project = project;
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
		// If we've already traversd this module, don't do it again
		if(objectFiles.containsKey(moduleName))
			return;
		
		// If it's a module that should be ignored, do that
		if(Arrays.binarySearch(ignoredModules, moduleName) >= 0)
			return;
		
		System.out.println("collectRecursive(" + moduleName + ");");
		
		int index = moduleName.lastIndexOf('.');
		String packagePart = index > 0 ? moduleName.substring(0, index) : "";
		String modulePart = String.format("%1$s.d", index > 0 ? 
				moduleName.substring(index + 1) : moduleName);
		
		System.out.println(packagePart + " -- " + modulePart);
		
		// Find the module
		ICompilationUnit module = null;
		boolean isLibraryFile = false;
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
					isLibraryFile = true;
				}
				
				if(module.exists())
				{
					break;
				}
			}
		}
		
		if(null == module || !module.exists())
			throw new BuildFailedException("Could not find module %$1s");
		
		objectFiles.put(moduleName, createObjectFile(module, isLibraryFile));
		IImportDeclaration[] imports = module.getImports();
		for(IImportDeclaration importDecl : imports)
			collectRecursive(importDecl.getElementName());
	}
	
	private ObjectFile createObjectFile(ICompilationUnit cu, boolean isLibraryFile)
	{
		// FIXME CONTINUE WORK HERE
		return new ObjectFile(project, cu.getPath().toFile(), 
				cu.getFullyQualifiedName(), isLibraryFile);
	}
	
	static
	{
		ignoredModules = new String[]
		{
			"object"
		};
		Arrays.sort(ignoredModules);
	}
}
