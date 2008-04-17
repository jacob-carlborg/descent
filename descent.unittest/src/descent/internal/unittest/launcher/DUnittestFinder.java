package descent.internal.unittest.launcher;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import descent.core.ICompilationUnit;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.IParent;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.unittest.ITestSpecification;

public class DUnittestFinder
{
	/**
	 * Expected number of unit tests.
	 */
	static final int LIST_PREALLOC = 10;
	
	public static void findTestsInContainer(
	        IJavaElement container,
			List<ITestSpecification> result,
			IProgressMonitor pm,
			boolean includeSubpackages)
	{
	    try
	    {
			if (container instanceof IJavaProject) {
				IJavaProject project= (IJavaProject) container;
				findTestsInProject(project, result, pm);
			} else if (container instanceof IPackageFragmentRoot) {
				IPackageFragmentRoot root= (IPackageFragmentRoot) container;
				findTestsInPackageFragmentRoot(root, result, pm);
			} else if (container instanceof IPackageFragment) {
				IPackageFragment fragment= (IPackageFragment) container;
				if(includeSubpackages)
				    findTestsInPackageAndSubpackages(fragment, result, pm);
				else
				    findTestsInPackageFragment(fragment, result, pm);
			} else if (container instanceof ICompilationUnit) {
				ICompilationUnit module= (ICompilationUnit) container;
				pm.beginTask("Finding unit tests", 1);
				findTestsInCompilationUnit(module, result);
				pm.worked(1);
				pm.done();
			}	
		} catch(JavaModelException e) {
			// do nothing
		}
	}
	
	private static void findTestsInProject(
	        IJavaProject project,
			List<ITestSpecification> result,
			IProgressMonitor pm) throws JavaModelException {
		IPackageFragmentRoot[] roots= project.getPackageFragmentRoots();
		pm.beginTask("Finding unit tests", 100 * roots.length);
		for (int i= 0; i < roots.length; i++) {
			IPackageFragmentRoot root= roots[i];
			findTestsInPackageFragmentRoot(root, result,
					new SubProgressMonitor(pm, 100));
		}
		pm.done();
	}

	private static void findTestsInPackageFragmentRoot(
	        IPackageFragmentRoot root, 
			List<ITestSpecification> result,
			IProgressMonitor pm) throws JavaModelException {
		IJavaElement[] children= root.getChildren();
		pm.beginTask("Finding unit tests", 100 * children.length);
		for (int j= 0; j < children.length; j++) {
			IPackageFragment fragment= (IPackageFragment) children[j];
			findTestsInPackageFragment(fragment, result, 
			        new SubProgressMonitor(pm, 100));
		}
		pm.done();
	}
	
	private static void findTestsInPackageAndSubpackages(
	        IPackageFragment fragment,
            List<ITestSpecification> result,
            IProgressMonitor pm) throws JavaModelException
	{
	    // If it's the default package, include everything in the source folder
	    if(fragment.isDefaultPackage())
	    {
	        findTestsInPackageFragmentRoot((IPackageFragmentRoot)
	                fragment.getParent(), result, pm);
	        return;
	    }
	    
	    // Otherwise, manually find out which packages are subpackages
	    String packageName = fragment.getElementName();
        List<IPackageFragment> subpackages = new ArrayList<IPackageFragment>();
        IPackageFragmentRoot parent = (IPackageFragmentRoot) fragment.getParent();
        IJavaElement[] allPackages = parent.getChildren();
        
        for(int i = 0; i < allPackages.length; i++)
        {
            if(null == allPackages[i] || !(allPackages[i] instanceof IPackageFragment))
                continue;
            
            IPackageFragment pkg = (IPackageFragment) allPackages[i];
            if(pkg.getElementName().startsWith(packageName))
                subpackages.add(pkg);
        }
        
        pm.beginTask("Finding unit tests", 100 * subpackages.size());
        for(IPackageFragment pkg : subpackages)
            findTestsInPackageFragment(pkg, result, new SubProgressMonitor(pm, 100));
        pm.done();
	}
	
	private static void findTestsInPackageFragment(
	        IPackageFragment fragment,
			List<ITestSpecification> result,
			IProgressMonitor pm) throws JavaModelException
	{   
		ICompilationUnit[] compilationUnits= fragment.getCompilationUnits();
		pm.beginTask("Finding unit tests", compilationUnits.length);
		for (int k= 0; k < compilationUnits.length; k++)
		{
			ICompilationUnit module = compilationUnits[k];
			findTestsInCompilationUnit(module, result);
			pm.worked(1);
		}
		pm.done();
	}
	
	private static void findTestsInCompilationUnit(
	        ICompilationUnit module,
			List<ITestSpecification> result) throws JavaModelException
	{
		testSearch(result, module.getFullyQualifiedName(), module);
	}
	
	private static void testSearch(List<ITestSpecification> result, String prefix,
			IParent node) throws JavaModelException
	{
		short count = 0;
		IJavaElement[] children = node.getChildren();
		for(IJavaElement child : children)
		{
			if(child instanceof IType)
			{
				IType type = (IType) child;
				testSearch(result, prefix + "." + type.getElementName(), type);
			}
			
			if(child instanceof IInitializer)
			{
				IInitializer init = (IInitializer) child;
				if(init.isUnitTest())
				{
					String id = prefix + "." + count;
					String name = id; // NEXTVERSION real names
					result.add(new TestSpecification(id, name, init));
					count++;
				}
			}
		}
	}
}
