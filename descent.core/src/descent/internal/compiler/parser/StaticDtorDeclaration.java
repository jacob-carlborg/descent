package descent.internal.compiler.parser;

public class StaticDtorDeclaration extends FuncDeclaration {

	@Override
	public int kind() {
		return STATIC_DTOR_DECLARATION;
	}
	
}
