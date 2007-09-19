package dtool.tests.ast.converter;

import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IOpenable;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ISourceModule;
import org.junit.Test;

/**
 * Test conversion of Phobos srcs
 */
public class Convertion_PhobosTest extends ConvertionCommonTest {

	
	@Test
	public void testPhobosHeaders() throws CoreException {
		IFolder folder = 
			SampleMainProject.project.getFolder(SampleMainProject.TEST_SRC_PHOBOSHD);
		IProjectFragment fragment = SampleMainProject.deeProj.getProjectFragment(folder);
		//parseElement((LangElement) sourceRoot);
		parseElement(fragment);
	}

	@Test
	public void testPhobosInternal() throws CoreException {
		IFolder folder = 
			SampleMainProject.project.getFolder(SampleMainProject.TEST_SRC_PHOBOSIMPL);
		IProjectFragment fragment = SampleMainProject.deeProj.getProjectFragment(folder);
		IProjectFragment sourceRoot = fragment;
		//parseElement((LangElement) sourceRoot);
		parseElement(sourceRoot);
	}
	
	@Test
	public void testTango() throws CoreException {
		IFolder folder = 
			SampleMainProject.project.getFolder(SampleMainProject.TEST_SRC_TANGO);
		IProjectFragment fragment = SampleMainProject.deeProj.getProjectFragment(folder);
		IProjectFragment sourceRoot = fragment;
		//parseElement((DeeSourceFolder) sourceRoot);
		parseElement(sourceRoot);
	}
	
	public static void parseElement(IOpenable langElement) throws CoreException {
		//Logg.model.println("Parse ModelElement Recursive: " + langElement.getPath());
		langElement.open(null);
		if(langElement instanceof ISourceModule)
			return;
		for(IModelElement child : ((IParent) langElement).getChildren()) {
			parseElement((IOpenable) child);
		}
	}

}
