package descent.internal.core.dom;


public abstract class Declaration extends Dsymbol {

	public String comments;
	public int storage_class;
	
	public Declaration(Identifier id) {
		this.ident = id;
	}

}
