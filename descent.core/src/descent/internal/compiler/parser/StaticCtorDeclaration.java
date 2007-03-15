package descent.internal.compiler.parser;

public class StaticCtorDeclaration extends FuncDeclaration {
	
	@Override
	public int kind() {
		return STATIC_CTOR_DECLARATION;
	}

}
