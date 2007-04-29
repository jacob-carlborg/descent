package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.AssertExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpAssert extends Expression {
	
	public Expression exp;
	public Expression msg;

	public ExpAssert(AssertExp element) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, exp);
			TreeVisitor.acceptChild(visitor, msg);
		}
		visitor.endVisit(this);
	}

}
