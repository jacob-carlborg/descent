package mmrnmhrm.tests.core.ref;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.junit.AfterClass;

import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.tests.BasePluginTest;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.DeeEditor;


/** 
 * Common test class for a UI test which works on a single editor.
 */
public class UITestWithEditor extends BasePluginTest {

	protected static IFile file;
	protected static CompilationUnit cunit;
	protected static DeeEditor editor;

	
	protected static void setupWithFile(DeeProject deeProject, String path) throws PartInitException, CoreException {
		IWorkbenchPage page = DeePlugin.getActivePage();
		IProject project = deeProject.getProject();
		file = project.getFile(path);
		editor = (DeeEditor) IDE.openEditor(page, file, DeeEditor.EDITOR_ID);
		cunit =	DeePlugin.getInstance().getCompilationUnit(editor.getEditorInput());
		
		
	}

	@AfterClass
	public static void tearDown() {
		editor.close(false);
	}

}