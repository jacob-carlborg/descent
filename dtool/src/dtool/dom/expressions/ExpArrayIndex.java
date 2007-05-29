package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.ArrayExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpArrayIndex extends Expression {

	public Expression array;
	public Expression[] args;
	
	public ExpArrayIndex(ArrayExp elem) {
		convertNode(elem);
		this.array = Expression.convert(elem.getExpression());
		this.args = Expression.convertMany(elem.getArguments());
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, array);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);	 
	}

}
