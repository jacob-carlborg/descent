package descent.internal.compiler.parser;

public class DtorDeclaration extends FuncDeclaration {
	
	@Override
	public int kind() {
		return DTOR_DECLARATION;
	}

}
