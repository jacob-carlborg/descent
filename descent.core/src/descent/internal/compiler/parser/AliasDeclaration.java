package descent.internal.compiler.parser;


public class AliasDeclaration extends Declaration {
	
	public boolean last; // is this the last declaration in a multi declaration?
	public Type type;
	
	public AliasDeclaration(IdentifierExp ident, Type type) {
		super(ident);
		this.type = type;
	}
	
	@Override
	public int kind() {
		return ALIAS_DECLARATION;
	}

}
