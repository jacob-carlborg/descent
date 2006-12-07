package descent.internal.core.dom;

import descent.core.dom.IDeclaration;

public abstract class Declaration extends Dsymbol implements IDeclaration {

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
