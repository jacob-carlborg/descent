package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.AssertExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpAssert extends Expression {
	
	public Resolvable exp;
	public Resolvable msg;

	public ExpAssert(AssertExp elem) {
		convertNode(elem);
		this.exp = Expression.convert(elem.e1);
		this.msg = Expression.convert(elem.msg);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, msg);
		}
		visitor.endVisit(this);
	}

}
