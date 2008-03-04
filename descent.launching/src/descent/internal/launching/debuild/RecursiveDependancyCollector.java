package descent.internal.launching.debuild;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.ICompilationUnit;
import descent.core.IJavaProject;

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
			ICompilationUnit[] modules,
			IProgressMonitor pm)
	{
		RecursiveDependancyCollector collector = new RecursiveDependancyCollector(project);
		return collector.collect(modules, pm);
	}
	
	private final Set<ObjectFile> objectFiles = new HashSet<ObjectFile>();
	private final IJavaProject project;
	
	// Shouldn't be constructed directly, use the getObjectFiles method instead
	private RecursiveDependancyCollector(IJavaProject project)
	{
		this.project = project;
	}
	
	private ObjectFile[] collect(ICompilationUnit[] modules, IProgressMonitor pm)
	{
		try
		{
			pm.beginTask("Collecting module dependancies", modules.length * 10);
			
			for(ICompilationUnit module : modules)
			{
				ObjectFile obj = createObjectFile(module);
				if(objectFiles.contains(obj))
				{
					pm.worked(10);
					continue;
				}
				
				// TODO recurse through imports, etc.
			}
			
			return objectFiles.toArray(new ObjectFile[objectFiles.size()]);
		}
		finally
		{
			pm.done();
		}
	}
	
	private ObjectFile createObjectFile(ICompilationUnit cu)
	{
		String moduleName = cu.getFullyQualifiedName();
		File inputFile = new File(cu.getResource().getLocation().makeAbsolute()
				.toPortableString());
		return new ObjectFile(project, inputFile, moduleName);
	}
}
