package descent.internal.compiler.parser;

public class VarDeclaration extends Declaration {

	public boolean last; // is this the last declaration in a multi declaration?
	public final Type type;
	public final Initializer init;
	

	public VarDeclaration(Type type, IdentifierExp ident, Initializer init) {
		this.type = type;
		this.ident = ident;
		this.init = init;		
	}
	
	@Override
	public int kind() {
		return VAR_DECLARATION;
	}

}
