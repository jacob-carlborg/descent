package descent.internal.compiler.parser;

public abstract class Declaration extends Dsymbol {
	
	public Type type;
	public int storage_class;
	public LINK linkage;
	public PROT protection;
	
	public Declaration() {
		this(null);
	}
	
	public Declaration(IdentifierExp ident) {
		this.ident = ident;
		type = null;
	    storage_class = STC.STCundefined;
	    protection = PROT.PROTundefined;
	    linkage = LINK.LINKdefault;
	}
	
	@Override
	public Declaration isDeclaration() {
		return this;
	}
	
	public boolean isDataseg(SemanticContext context) {
		return false;
	}

}
