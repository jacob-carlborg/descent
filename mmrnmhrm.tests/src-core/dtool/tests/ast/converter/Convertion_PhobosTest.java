package dtool.tests.ast.converter;

import melnorme.miscutil.log.Logg;
import mmrnmhrm.core.model.IDeeSourceRoot;
import mmrnmhrm.core.model.lang.ILangElement;
import mmrnmhrm.core.model.lang.LangElement;
import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

/**
 * Test conversion of Phobos srcs
 */
public class Convertion_PhobosTest extends ConvertionCommonTest {

	
	@Test
	public void testPhobosHeaders() throws CoreException {
		IFolder folder = 
			SampleMainProject.project.getFolder(SampleMainProject.TEST_SRC_PHOBOSHD);
		IDeeSourceRoot sourceRoot = SampleMainProject.deeProj.getSourceRoot(folder);
		parseElement((LangElement) sourceRoot);
	}

	@Test
	public void testPhobosInternal() throws CoreException {
		IFolder folder = 
			SampleMainProject.project.getFolder(SampleMainProject.TEST_SRC_PHOBOSIMPL);
		IDeeSourceRoot sourceRoot = SampleMainProject.deeProj.getSourceRoot(folder);
		parseElement((LangElement) sourceRoot);
	}
	
	@Test
	public void testTango() throws CoreException {
		IFolder folder = 
			SampleMainProject.project.getFolder(SampleMainProject.TEST_SRC_TANGO);
		IDeeSourceRoot sourceRoot = SampleMainProject.deeProj.getSourceRoot(folder);
		parseElement((LangElement) sourceRoot);
	}
	
	public static void parseElement(LangElement langElement) throws CoreException {
		Logg.model.println("Parse Element Recursive: " + langElement.getUnderlyingResource());
		langElement.updateElementLazily(); // dispose structure
		langElement.getElementInfo(); // recreate structure
		for(ILangElement child : langElement.getLangChildren()) {
			LangElement childElem = ((LangElement) child);
			parseElement(childElem);
			// dispose structure to prevent out of memory
			childElem.updateElementLazily(); 
		}
	}

}
