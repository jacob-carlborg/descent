package descent.internal.debug.ui.console;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaCore;
import descent.core.JavaModelException;

/*package*/ class ResourceSearch
{
	public static IFile search(String filename)
	{
		// TODO -- REALLY stupid algorithm. Every time you click, it recreates a ternary tree
		// of the entire workspace, and then does prefix searches of the path with the beginnings
		// chopped off until it finds the file
		IPath path  = new Path(filename);
		try
		{
			TernaryTree<IFile> tree = new TernaryTree<IFile>();
			enumerateProjects(ResourcesPlugin.getWorkspace().getRoot().members(), tree);
			while(path.segmentCount() > 0)
			{
				String prefix = reverse(path.toPortableString());
				// TODO what to do about multiple matches?
				for(IFile result : tree.prefixSearch(prefix))
					return result;
				path = path.removeFirstSegments(1);
			}
			return null;
		}
		catch(CoreException e)
		{
			return null;
		}
	}
	
	private static void enumerateProjects(IResource[] resources, TernaryTree<IFile> tree) throws CoreException
	{
		for(IResource res : resources)
		{
			if(res instanceof IProject)
			{
				IProject proj = ((IProject) res).getProject();
				if(!proj.isOpen())
					continue;
				
				// Make sure it's a D project
				IJavaProject javaProj;
				try
				{
					javaProj = (IJavaProject) proj.getNature(JavaCore.NATURE_ID);
				}
				catch(Exception e)
				{
					continue;
				}
				enumeratePFRs(javaProj.getPackageFragmentRoots(), tree);
			}
		}
	}
	
	private static void enumeratePFRs(IPackageFragmentRoot[] elements, TernaryTree<IFile> tree)
	{
		for(IPackageFragmentRoot root : elements)
		{
			try
			{
				enumeratePackageFragments(root.getChildren(), tree);
			}
			catch(JavaModelException e)
			{
				continue;
			}
		}
	}
	
	
	
	private static void enumeratePackageFragments(IJavaElement[] elements, TernaryTree<IFile> tree)
	{
		for(IJavaElement fragment : elements)
		{
			try
			{
				if(fragment instanceof IPackageFragment)
					enumerateCompilationUnits(((IPackageFragment) fragment).getCompilationUnits(), tree);
			}
			catch(JavaModelException e)
			{
				continue;
			}
		}
	}

	private static void enumerateCompilationUnits(ICompilationUnit[] modules, TernaryTree<IFile> tree)
	{
		for(ICompilationUnit module : modules)
		{
			IResource res = module.getResource();
			if(null == res || !(res instanceof IFile))
				continue;
			IFile file = (IFile) res;
			tree.add(reverse(file.getFullPath().toPortableString()), file);
		}
	}

	private static String reverse(String s)
	{
		return ((new StringBuffer(s)).reverse()).toString();
	}
}
