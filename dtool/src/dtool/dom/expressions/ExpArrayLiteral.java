package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.ArrayLiteralExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpArrayLiteral extends Expression {
	
	public Expression[] args;

	public ExpArrayLiteral(ArrayLiteralExp elem) {
		convertNode(elem);
		this.args = Expression.convertMany(elem.getArguments());
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);	 
	}

}
