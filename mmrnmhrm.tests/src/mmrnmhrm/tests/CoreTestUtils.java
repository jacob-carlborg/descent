package mmrnmhrm.tests;

import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.EModelStatus;
import mmrnmhrm.tests.adapters.Mock_IFile;

import org.eclipse.core.runtime.CoreException;

import util.StringUtil;

public class CoreTestUtils {

	
	public static CompilationUnit testCUparsing(String source) throws CoreException {

		CompilationUnit cunit = new CompilationUnit(new Mock_IFile());
		cunit.updateElement();
		cunit.getDocument().set(source);
		cunit.reconcile();
		BaseTestClass.assertTrue(cunit.parseStatus == EModelStatus.OK,
				"Module failed to parse Correctly" + 
				"\n " + StringUtil.collToString(cunit.problems, "\n "));
		//System.out.print(ASTPrinter.toStringAST(cunit.getModule()));
		return cunit;
	}
}
