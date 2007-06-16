package descent.internal.ui.text.correction;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.ICompilationUnit;
import descent.core.dom.ASTParser;
import descent.core.dom.CompilationUnit;
import descent.internal.ui.javaeditor.ASTProvider;

// TODO JDT UI ast put back all the methods
public class ASTResolving {

	public static CompilationUnit createQuickFixAST(ICompilationUnit compilationUnit, IProgressMonitor monitor) {
		ASTParser astParser= ASTParser.newParser(ASTProvider.SHARED_AST_LEVEL);
		astParser.setSource(compilationUnit);
		astParser.setResolveBindings(true);
		astParser.setStatementsRecovery(ASTProvider.SHARED_AST_STATEMENT_RECOVERY);
		return (CompilationUnit) astParser.createAST(monitor);
	}

}
