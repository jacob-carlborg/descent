package descent.tests.model;

import descent.core.ICompilationUnit;
import descent.core.compiler.IProblem;
import descent.core.dom.AST;
import descent.core.dom.ASTParser;
import descent.core.dom.CompilationUnit;

public class SemanticTest extends AbstractModelTest {
	
	public void testEnum_BaseTypeMustBeOfIntegralType_Easy() throws Exception {
		ICompilationUnit unit = createCompilationUnit("test.d", " enum some : double { x }");
		
		ASTParser parser = ASTParser.newParser(AST.D2);
		parser.setResolveBindings(true);
		parser.setSource(unit);
		CompilationUnit cUnit = (CompilationUnit) parser.createAST(null);
		assertEquals(1, cUnit.getProblems().length);
		assertError(cUnit.getProblems()[0], IProblem.BaseTypeMustBeOfIntegralType, 13, 6);
	}
	
	protected void assertError(IProblem p, int errorCode, int start, int length) {
		assertEquals(errorCode, p.getID());
		assertTrue(p.isError());
		assertEquals(start, p.getSourceStart());
		assertEquals(length, p.getSourceEnd() - p.getSourceStart() + 1);
	}
	
	protected void assertWarning(IProblem p, int errorCode, int start, int length) {
		assertEquals(errorCode, p.getID());
		assertTrue(p.isError());
		assertEquals(start, p.getSourceStart());
		assertEquals(length, p.getSourceEnd() - p.getSourceStart() + 1);
	}

}
