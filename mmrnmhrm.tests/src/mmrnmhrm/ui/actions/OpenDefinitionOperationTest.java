package mmrnmhrm.ui.actions;

import junit.framework.Assert;
import melnorme.lang.ui.EditorUtil;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.tests.BaseUITest;
import mmrnmhrm.tests.SampleNonDeeProject;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class OpenDefinitionOperationTest extends BaseUITest {

	private static final String TEST_SRCFILE = SampleMainProject.TEST_SRC1 + "/testGoToDefOp.d";
	private static final String TEST_SRC_TARGETFILE = SampleMainProject.TEST_SRC3 +"/pack/sample.d";

	private static final String TEST_OUTSRCFILE = SampleMainProject.TEST_OUT_SRC
			+ "/testGoToDefOp.d";

	protected IFile file; 
	protected CompilationUnit cunit;
	protected IEditorPart editor;
	protected ITextEditor srcEditor;
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		OperationsManager.get().unitTestMode = true;
	}

	@Before
	public void setUp() throws Exception {
		IProject project = SampleMainProject.sampleDeeProj.getProject();
		setupWithFile(project, TEST_SRCFILE);
	}

	private void setupWithFile(IProject project, String path) throws PartInitException, CoreException {
		IWorkbenchPage page = DeePlugin.getActivePage();
		file = project.getFile(path);
		editor = IDE.openEditor(page, file, DeeEditor.EDITOR_ID);
		srcEditor = (ITextEditor) editor;
		cunit =	DeePlugin.getInstance().getCompilationUnit(editor.getEditorInput());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() {
		// find target in same file
		doTest(123, IStatus.OK, SampleMainProject.project, TEST_SRCFILE); 
	}
	
	@Test
	public void test2() {
		// not found
		doTest(135, IStatus.WARNING, SampleMainProject.project, TEST_SRCFILE); 
	}
	
	@Test
	public void test3() {		
		// already a def
		doTest(54, IStatus.INFO, SampleMainProject.project, TEST_SRCFILE); 
	}
	
	@Test
	public void test4() {
		// find target in other file
		doTest(157, IStatus.OK, SampleMainProject.project, TEST_SRC_TARGETFILE); 
	}

	@Test
	public void testOutside() throws CoreException {
		IProject project = SampleMainProject.project;
		setupWithFile(project, TEST_OUTSRCFILE);
		doTest(123, IStatus.OK, project, TEST_OUTSRCFILE);
	}
	
	@Test
	public void testOutside2() throws CoreException {
		IProject project = SampleMainProject.project;
		setupWithFile(project, TEST_OUTSRCFILE);
		doTest(157, IStatus.OK, project, TEST_SRC_TARGETFILE);
	}
	
	
	@Test
	public void testReallyOutside() throws CoreException {
		IProject project = SampleNonDeeProject.project;
		setupWithFile(project, TEST_OUTSRCFILE);
		doTest(123, IStatus.OK, project, TEST_OUTSRCFILE);
	}
	
	@Test
	public void testReallyOutside2() throws CoreException {
		IProject project = SampleNonDeeProject.project;
		setupWithFile(project, TEST_OUTSRCFILE);
		doTest(157, IStatus.WARNING, project, TEST_OUTSRCFILE);
	}

	private void doTest(int offset, int result, IProject project, String editorFile) {
		EditorUtil.setSelection(srcEditor, offset, 0);
		GoToDefinitionHandler.executeChecked(srcEditor, true);
		assertTrue(OperationsManager.get().opResult == result, "Got result: " + result);
		assertCurrentEditorIsEditing(project.getFullPath(), editorFile);
	}
	
	private void assertCurrentEditorIsEditing(IPath prjpath, String targetpath) {
		DeeEditor deeEditor;
		deeEditor = (DeeEditor) DeePlugin.getActivePage().getActiveEditor();
		IPath path = deeEditor.getCompilationUnit().getFile().getFullPath();
		Assert.assertEquals(path, prjpath.append(targetpath));
	}


}
