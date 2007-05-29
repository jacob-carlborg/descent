package dtool.dom.expressions;

import descent.core.dom.IFalseExpression;
import descent.core.dom.ITrueExpression;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpLiteralBool extends Expression {
	
	boolean value;

	public ExpLiteralBool(ITrueExpression elem) {
		convertNode((ASTNode)elem);
		this.value = true;
	}

	public ExpLiteralBool(IFalseExpression elem) {
		convertNode((ASTNode)elem);
		this.value = false;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	
	}

}
