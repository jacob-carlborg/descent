package descent.tests.binding;

import descent.core.ICompilationUnit;
import descent.core.dom.AST;
import descent.core.dom.ASTParser;
import descent.core.dom.CompilationUnit;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.tests.model.AbstractModelTest;

public abstract class AbstractBinding_Test extends AbstractModelTest implements ISignatureConstants {
	
	protected ICompilationUnit lastCompilationUnit;
	protected CompilationUnit createCU(String filename, String source) throws Exception {
		lastCompilationUnit = createCompilationUnit(filename, source);
		
		ASTParser parser = ASTParser.newParser(AST.D2);
		parser.setSource(lastCompilationUnit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}

}
