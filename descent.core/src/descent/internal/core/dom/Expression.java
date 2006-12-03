package descent.internal.core.dom;

import descent.core.dom.IExpression;

public abstract class Expression extends ASTNode implements IExpression {

	public Expression() {
		
	}
	
	Expression(AST ast) {
		super(ast);
	}
	
}
