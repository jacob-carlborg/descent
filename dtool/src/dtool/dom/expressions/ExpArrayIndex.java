package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.ArrayExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpArrayIndex extends Expression {

	public Expression array;
	public Expression[] args;
	
	
	public ExpArrayIndex(ArrayExp element) {
		this.array = Expression.convert(element.getExpression());
		this.args = Expression.convertMany(element.getArguments());
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
