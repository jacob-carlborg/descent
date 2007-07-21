package mmrnmhrm.ui.editor;

import mmrnmhrm.tests.SampleProjectTest;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.views.ASTViewer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeeEditorTest extends SampleProjectTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testDeeEditor() throws CoreException {
		IFile file = sampleFile1;
		
		IWorkbenchPage page = DeePlugin.getActivePage();
		IEditorPart editor = IDE.openEditor(page, file, DeeEditor.EDITOR_ID);
		assertTrue(editor instanceof DeeEditor);

		page.showView("org.eclipse.ui.views.ContentOutline");
		page.showView(ASTViewer.VIEW_ID);
	}
	
	@Test
	public void testDeeEditor2() throws CoreException {
		IFile file = sampleOutOfModelFile;
		
		IWorkbenchPage page = DeePlugin.getActivePage();
		IEditorPart editor = IDE.openEditor(page, file, DeeEditor.EDITOR_ID);
		assertTrue(editor instanceof DeeEditor);

		page.showView("org.eclipse.ui.views.ContentOutline");
		page.showView(ASTViewer.VIEW_ID);
	}
	
	@Test
	public void testDeeEditor3() throws CoreException {
		IWorkbenchPage page = DeePlugin.getActivePage();
		IEditorPart editor = 
			IDE.openEditor(page, sampleNonExistantFile, DeeEditor.EDITOR_ID);
		assertTrue(!(editor instanceof DeeEditor));
		assertTrue(exceptionThrown == true);
		exceptionThrown = false;
	}

}
