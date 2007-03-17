package descent.internal.compiler.parser;

public abstract class Declaration extends Dsymbol {
	
	public Type type;
	public int storage_class;
	public LINK linkage;
	
	public Declaration() {
	}
	
	public Declaration(IdentifierExp ident) {
		this.ident = ident;
	}
	
	public boolean isDataseg(SemanticContext context) {
		return false;
	}

}
