package descent.tests.model;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaCore;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnit;

public class ModelTest extends TestCase {
	
	private IProject project;
	
	@Override
	protected void setUp() throws Exception {
		project = createProject("D");
	}
	
	public void testEmpty() throws Exception {
		IJavaProject javaProject = JavaCore.create(project);
		assertNotNull(javaProject);
		
		assertFalse(javaProject.isOpen());
		
		javaProject.open(null);
		assertTrue(javaProject.isOpen());
		
		IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
		assertEquals(1, roots.length);
		
		IPackageFragmentRoot root = roots[0];
		assertFalse(root.isOpen());
		
		root.open(null);
		assertTrue(root.isOpen());
		
		IJavaElement[] children = root.getChildren();
		assertEquals(1, children.length);
		
		IPackageFragment pack = (IPackageFragment) children[0];
		assertFalse(pack.isOpen());
		
		pack.open(null);
		assertTrue(pack.isOpen());
		
		ICompilationUnit unit = pack.createCompilationUnit("test.d", "module a;", true, null);
		assertTrue(unit.exists());
		
		assertFalse(unit.isOpen());
		
		unit.open(null);
		assertTrue(unit.isOpen());
		
		assertFalse(unit.isWorkingCopy());
		
		unit.becomeWorkingCopy(null, null);
		assertTrue(unit.isWorkingCopy());
		
		CompilationUnit ast = unit.reconcile(AST.D1, true, null, null);
		assertNotNull(ast);
	}
	
	private IProject createProject(String name) throws CoreException {
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

}
