package mmrnmhrm.tests.core.ref;

import melnorme.miscutil.Assert;
import mmrnmhrm.tests.BaseUITest;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.junit.AfterClass;


/** 
 * Common test class for a UI test which works on a single editor.
 */
public class UITestWithEditor extends BaseUITest {

	protected static IFile file;
	protected static ScriptEditor editor;
	protected static ISourceModule srcModule;

	
	protected static void setupWithFile(IScriptProject deeProject, String path) throws PartInitException, CoreException {
		IWorkbenchPage page = DeePlugin.getActivePage();
		IProject project = deeProject.getProject();
		file = project.getFile(path);
		editor = (ScriptEditor) IDE.openEditor(page, file, DeeEditor.EDITOR_ID);
		Assert.isTrue(editor.getScriptSourceViewer() != null);
		srcModule = DLTKCore.createSourceModuleFrom(file);
	}

	@AfterClass
	public static void tearDown() {
		editor.close(false);
	}

}