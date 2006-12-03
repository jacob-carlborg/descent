package descent.internal.core.dom;

import descent.core.dom.IStatement;

public abstract class Statement extends ASTNode implements IStatement {
	
	public Statement() {
		
	}
	
	Statement(AST ast) {
		super(ast);
	}

}
