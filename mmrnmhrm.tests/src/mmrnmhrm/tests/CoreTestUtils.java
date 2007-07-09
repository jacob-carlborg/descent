package mmrnmhrm.tests;

import melnorme.miscutil.StringUtil;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.EModelStatus;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;


public class CoreTestUtils {

	
	public static CompilationUnit testParseCUnit(final String cunitsource) throws CoreException {

		// create an orphaned Compilation Unit
		CompilationUnit cunit = new CompilationUnit(null) {
			@Override
			public String getSource() {
				return cunitsource;
			}
			
			@Override
			protected void clearErrorMarkers() {
				// do nothing
			}
		};
		//cunit.updateElement();
		//cunit.getDocument().set(cunitsource);
		cunit.reconcile();
		BaseTestClass.assertTrue(cunit.parseStatus == EModelStatus.OK,
				"Module failed to parse Correctly" + 
				"\n " + StringUtil.collToString(cunit.problems, "\n "));
		//System.out.print(ASTPrinter.toStringAST(cunit.getModule()));
		return cunit;
	}
}
