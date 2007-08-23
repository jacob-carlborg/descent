package mmrnmhrm.tests.core;

import java.util.Arrays;
import java.util.Collection;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModelRoot;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.tests.BasePluginTest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class ModelTest extends BasePluginTest {

	private static IWorkspaceRoot wroot;
	private static DeeModelRoot droot;
	private static Collection<DeeProject> otherProjs;	
	
	@BeforeClass
	public static void setUp() throws CoreException{
		wroot = DeeCore.getWorkspaceRoot();
		droot = DeeModelRoot.getInstance();

		otherProjs = Arrays.asList(droot.getLangProjects());
		for (DeeProject dproj : otherProjs) {
			dproj.getProject().close(null);
		}
		droot.updateElementLazily();
	}

	@AfterClass
	public static void setDown() throws CoreException{
		for (IProject proj : wroot.getProjects()) {
			if(!proj.isOpen())
				proj.open(null);
		}
		droot.updateElementLazily();
	}

	/*
	@Test
	public void test() throws CoreException {
		//XXX: todo, maybe run as a model operation
		//if(true)return;
		DeeModel.getRoot().updateElementRecursive();
		DeeModel.fireModelChanged();
		
		IProject project;
		IFolder folder;
		DeeProject deeproj;
		
		project = createProject("proj1");
		droot.createDeeProject(project);
		checkResourcesEquals(getTestSourceFolders(), "proj1");
		
		project = createProject("proj2"); 
		deeproj = droot.createDeeProject(project);
		checkResourcesEquals(getTestSourceFolders(), "proj1", "proj2");
		
		project = createProject("proj3"); 
		droot.createDeeProject(project);
		checkResourcesEquals(getTestSourceFolders(), "proj1", "proj2", "proj3");
		
		project.delete(false, null);
		droot.updateElementLazily();
		checkResourcesEquals(getTestSourceFolders(), "proj1", "proj2");
		
		checkResourcesEquals(deeproj.getSourceFolders(), "src");
		folder = deeproj.getProject().getFolder("src1");
		deeproj.addSourceRoot(new DeeSourceFolder(folder,deeproj));
		checkResourcesEquals(deeproj.getSourceFolders(), "src", "src1");
		checkResourcesEquals(deeproj.getSourceRoots(), "src", "src1");
		
		
		DeeModel.getRoot().updateElementRecursive();
		DeeModel.fireModelChanged();
	}

	private DeeProject[] getTestSourceFolders() {
		List<DeeProject> list = Arrays.asList(droot.getLangProjects());
		list.removeAll(otherProjs);
		return list.toArray(new DeeProject[0]);
	}

	private IProject createProject(String name) throws CoreException {
		IProject project;
		project = wroot.getProject(name); project.create(null); project.open(null);
		return project;
	}
	
	void checkResourcesEquals(ILangElement[] elems, String... names) {
		if(elems.length != names.length)
			assertTrue(false, "Expected element size: "+names.length
					+" got:"+elems.length);
		
		for (int i = 0; i < names.length; i++) {
			if(!elems[i].getElementName().equals(names[i]))
				assertTrue(false, "Name mismatch, expeect: "+names[i]
						+" got:"+elems[i]);
		}
	}
	*/
}

