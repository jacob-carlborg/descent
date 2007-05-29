package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.CondExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpCond extends Expression {

	public Expression predExp;
	public Expression trueExp;
	public Expression falseExp;

	public ExpCond(CondExp elem) {
		convertNode(elem);
		this.predExp = Expression.convert(elem.cond); 
		this.trueExp = Expression.convert(elem.t);
		this.falseExp = Expression.convert(elem.f); 
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, predExp);
			TreeVisitor.acceptChildren(visitor, trueExp);
			TreeVisitor.acceptChildren(visitor, falseExp);
		}
		visitor.endVisit(this);
	}

}
