package mmrnmhrm.ui.editor;

import mmrnmhrm.tests.BaseUITest;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.tests.UITestUtils;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.views.ASTViewer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeeEditorTest extends BaseUITest {

	public static IDocument getDocument(ScriptEditor editor) {
		return editor.getScriptSourceViewer().getDocument();
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testDeeEditor() throws CoreException {
		IFile file = SampleMainProject.sampleFile1;
		
		IWorkbenchPage page = DeePlugin.getActivePage();
		IEditorPart editor = IDE.openEditor(page, file, DeeEditorDLTK.EDITOR_ID);
		assertTrue(editor instanceof DeeEditorDLTK);

		page.showView("org.eclipse.ui.views.ContentOutline");
		page.showView(ASTViewer.VIEW_ID);
	}
	
	@Test
	public void testDeeEditor2() throws CoreException {
		IFile file = SampleMainProject.sampleOutOfModelFile;
		
		IWorkbenchPage page = DeePlugin.getActivePage();
		IEditorPart editor = IDE.openEditor(page, file, DeeEditorDLTK.EDITOR_ID);
		assertTrue(editor instanceof DeeEditorDLTK);

		page.showView("org.eclipse.ui.views.ContentOutline");
		page.showView(ASTViewer.VIEW_ID);
	}
	
	@Test
	public void testDeeEditor3() throws CoreException {
		IWorkbenchPage page = DeePlugin.getActivePage();
		IFile file = SampleMainProject.sampleNonExistantFile;
		IEditorPart editor = 
			IDE.openEditor(page, file, DeeEditorDLTK.EDITOR_ID);
		UITestUtils.runEventLoop(page.getActivePart().getSite().getShell());
		//assertTrue(!(editor instanceof DeeEditorDLTK));
		assertTrue(exceptionThrown == true);
		exceptionThrown = false;
	}

}
