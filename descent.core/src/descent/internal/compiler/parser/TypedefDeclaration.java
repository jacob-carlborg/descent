package descent.internal.compiler.parser;


public class TypedefDeclaration extends Declaration {
	
	public boolean last; // is this the last declaration in a multi declaration?
	public Type type;
	public Initializer initializer;	
	
	public TypedefDeclaration(IdentifierExp ident, Type type, Initializer init) {
		this.ident = ident;
		this.type = type;
		this.initializer = init;
	}
	
	@Override
	public int kind() {
		return TYPEDEF_DECLARATION;
	}

}
