package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class StaticIfCondition extends Condition {

	public Expression exp;

	public StaticIfCondition(Expression exp) {
		this.exp = exp;
	}
	
	@Override
	public int getConditionType() {
		return STATIC_IF;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
