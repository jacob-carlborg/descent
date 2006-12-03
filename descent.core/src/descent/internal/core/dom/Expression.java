package descent.internal.core.dom;

import descent.core.dom.IExpression;

public abstract class Expression extends ASTNode implements IExpression {

	public Expression() {
		super(AST.newAST(AST.JLS3));
	}
	
	Expression(AST ast) {
		super(ast);
	}
	
}
