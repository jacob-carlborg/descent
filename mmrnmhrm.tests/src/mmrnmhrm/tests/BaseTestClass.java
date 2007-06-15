package mmrnmhrm.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.EModelStatus;
import mmrnmhrm.ui.DeePlugin;

import util.FileUtil;
import util.StringUtil;
import junit.framework.Assert;

public class BaseTestClass {

	
	protected static void assertTrue(boolean b, String msg) {
		Assert.assertTrue(msg, b);
	}
	

	protected static String readStringFromResource(String filename, Class clss) throws IOException {
		InputStream is = clss.getResourceAsStream(filename);
		InputStreamReader isr = new InputStreamReader(is);
		return FileUtil.readStringFromReader(isr);
	}

	@Deprecated
	protected String readStringFromResource(String filename) throws IOException {
		return readStringFromResource(filename, this.getClass());
	}
	
	protected static CompilationUnit testCUparsing(String source) {
		CompilationUnit cunit = new CompilationUnit(new Mock_IFile());
		cunit.setSource(source);
		cunit.parseAST();
		assertTrue(cunit.parseStatus == EModelStatus.OK,
				"Module failed to parse Correctly" + 
				"\n " + StringUtil.collToString(cunit.problems, "\n "));
		//System.out.print(ASTPrinter.toStringAST(cunit.getModule()));
		return cunit;
	}
	
	protected static String getTestDataFileString(String pathstr) throws IOException {
		Bundle bundle = Platform.getBundle(DeePlugin.PLUGIN_ID);
		InputStream is = FileLocator.openStream(bundle, new Path("testdata/"+pathstr), false);
		InputStreamReader isr = new InputStreamReader(is);
		String src = FileUtil.readStringFromReader(isr);
		return src;
	}

	String str;
	
	
}