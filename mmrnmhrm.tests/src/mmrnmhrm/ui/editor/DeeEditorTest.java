package mmrnmhrm.ui.editor;

import java.io.ByteArrayInputStream;

import mmrnmhrm.tests.CommonProjectTestClass;
import mmrnmhrm.ui.DeePlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.ide.IDE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeeEditorTest extends CommonProjectTestClass{

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	String SAMPLEFILE = "foo.dm";

	String SAMPLEFILE_CONTENTS = "module pack.foo" +
			"int a;" +
			"" +
			"class Foo {}" +
			"void func() {" +
			"  int b = a;" +
			"" +
			"}";

	@Test
	public void testDeeEditor() throws CoreException {
		IFile file = sampleDeeProj.getProject().getFile(SAMPLEFILE);
		
		file.create(new ByteArrayInputStream(SAMPLEFILE_CONTENTS.getBytes()),
				false, null);
		
		IDE.openEditor(DeePlugin.getActivePage(), file);

	}

}
