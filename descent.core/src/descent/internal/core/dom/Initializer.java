package descent.internal.core.dom;

import descent.core.dom.IInitializer;

public abstract class Initializer extends ASTNode implements IInitializer {
	
	public Initializer() {
		super(AST.newAST(AST.JLS3));
	}
	
	Initializer(AST ast) {
		super(ast);
	}
	
}
