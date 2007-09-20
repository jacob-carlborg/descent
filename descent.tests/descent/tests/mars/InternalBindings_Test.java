package descent.tests.mars;

import descent.core.dom.AST;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.Module;

public class InternalBindings_Test extends Parser_Test {
	
	public void testClassDeclarationIdentifier() {
		Module m = getModuleSemanticNoProblems("class X { }", AST.D1);
		ClassDeclaration cd = (ClassDeclaration) m.members.get(0);
		assertSame(cd, cd.ident.getBinding());
	}
	
//	public void testAlias() {
//		Module m = getModuleSemanticNoProblems("class X { } alias X x;", AST.D1);
//		(C m.members.get(0);
//	}

}
