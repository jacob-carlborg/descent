package descent.tests.model;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import descent.core.IClasspathEntry;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.IParent;
import descent.core.JavaCore;

public abstract class AbstractModelTest extends TestCase {
	
	protected IProject project;
	protected IJavaProject javaProject;
	
	@Override
	protected void setUp() throws Exception {
		project = createProject("D");
		javaProject = JavaCore.create(project);
		
		javaProject.open(null);
		
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();		
		IClasspathEntry entry = JavaCore.newLibraryEntry(
				//new Path("c:\\ary\\programacion\\d\\1.020\\dmd\\src\\phobos"),
				new Path("C:\\d\\dmd_1.0.20\\dmd\\src\\phobos"),
				null, null);
		
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = entry;
		
		javaProject.setRawClasspath(newEntries, null);
	}
	
	@Override
	protected void tearDown() throws Exception {
		project.close(null);
		project.delete(IResource.ALWAYS_DELETE_PROJECT_CONTENT, null);
	}
	
	protected IProject createProject(String name) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();		
		IProject project = root.getProject(name);		
		IProjectDescription description = workspace.newProjectDescription(name);		
		project.create(description, null);
		
		if (!project.isOpen()) {
			project.open(null);
		}
		
		description = project.getDescription();
		
		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = JavaCore.NATURE_ID;
		
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
		
		return project;
	}
	
	protected ICompilationUnit createCompilationUnit(String packageName, String filename, String contents) throws Exception {
		//assertFalse(javaProject.isOpen());
		
		javaProject.open(null);
		assertTrue(javaProject.isOpen());
		
		IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
		//assertEquals(1, roots.length);
		
		IPackageFragmentRoot root = roots[0];
		//assertFalse(root.isOpen());
		
		root.open(null);
		assertTrue(root.isOpen());
		
		IJavaElement[] children = root.getChildren();
		//assertEquals(1, children.length);
		
		IPackageFragment pack;
		if (packageName != null) {
			pack = root.createPackageFragment(packageName, true, null);
		} else {
			pack = (IPackageFragment) children[0];
		}
		//assertFalse(pack.isOpen());
		
		pack.open(null);
		//assertTrue(pack.isOpen());
		
		ICompilationUnit unit = pack.createCompilationUnit(filename, contents, true, null);
		//assertTrue(unit.exists());
		
		//assertFalse(unit.isOpen());
		
		unit.open(null);
		//assertTrue(unit.isOpen());
		
		return unit;
	}
	
	protected ICompilationUnit createCompilationUnit(String filename, String contents) throws Exception {
		return createCompilationUnit(null, filename, contents);
	}
	
	protected void build() throws CoreException {
		javaProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}
	
	protected IJavaElement getVariable(IJavaElement unit, int num) throws Exception {
		return getElement(unit, num, IJavaElement.FIELD);
	}
	
	protected IJavaElement getFunction(IJavaElement unit, int num) throws Exception {
		return getElement(unit, num, IJavaElement.METHOD);
	}
	
	protected IJavaElement getElement(IJavaElement unit, int num, int type) throws Exception {
		int count = 0;
		
		for(IJavaElement child : ((IParent) unit).getChildren()) {
			if (child.getElementType() == type) {
				if (count == num) {
					return child;
				} else {
					count++;
				}
			}
		}
		return null;
	}

}
