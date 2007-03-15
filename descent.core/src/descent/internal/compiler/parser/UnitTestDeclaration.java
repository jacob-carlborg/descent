package descent.internal.compiler.parser;

public class UnitTestDeclaration extends FuncDeclaration {

	@Override
	public int kind() {
		return UNIT_TEST_DECLARATION;
	}
	
}
