package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.SliceExp;
import descent.internal.core.dom.TypeSlice;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpSlice extends Expression {

	public Expression slicee;
	public Expression from;
	public Expression to;
	
	public ExpSlice(SliceExp elem) {
		slicee = Expression.convert(elem.e);
		from = Expression.convert(elem.from);
		to = Expression.convert(elem.to);
	}
	
	public ExpSlice(TypeSlice elem) {
		//slicee = Expression.convert(elem.next);
		from = Expression.convert(elem.from);
		to = Expression.convert(elem.to);
	}
	

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, slicee);
			TreeVisitor.acceptChildren(visitor, from);
			TreeVisitor.acceptChildren(visitor, to);
		}
		visitor.endVisit(this);
	}

}
