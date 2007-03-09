package dtool.dom.expressions;

import util.tree.TreeVisitor;
import dtool.dom.ast.ASTNeoVisitor;

public abstract class PrefixExpression extends Expression {
	
	public Expression exp;

	public PrefixExpression(Expression exp) {
		this.exp = exp;
	}
	
	public interface PrefixExpressionTypes {
		
		int ADDRESS = 1;
		int PRE_INCREMENT = 2;
		int PRE_DECREMENT = 3;
		int POINTER = 4;
		int NEGATIVE = 5;
		int POSITIVE = 6;
		int NOT = 7;
		int INVERT = 8;
		int POST_INCREMENT = 9;
		int POST_DECREMENT = 10;
	}
	
	@Override
	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
