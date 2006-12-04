package descent.internal.core.dom;

public abstract class Declaration extends Dsymbol {

	public int storage_class;
	
	public Declaration() {
	}
	
	public Declaration(Identifier id) {
		this.ident = id;
	}
	
	Declaration(AST ast) {
		super(ast);
	}

}
