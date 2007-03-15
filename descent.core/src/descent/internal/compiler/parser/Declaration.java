package descent.internal.compiler.parser;

public abstract class Declaration extends Dsymbol {
	
	public Type type;
	public int storage_class;
	
	public Declaration() {
	}
	
	public Declaration(IdentifierExp ident) {
		this.ident = ident;
	}

}
