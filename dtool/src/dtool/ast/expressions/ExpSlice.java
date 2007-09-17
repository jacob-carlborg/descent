package dtool.ast.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.SliceExp;
import dtool.ast.IASTNeoVisitor;
import dtool.refmodel.IDefUnitReferenceNode;

public class ExpSlice extends Expression {

	public IDefUnitReferenceNode slicee;
	public Resolvable from;
	public Resolvable to;
	
	public ExpSlice(SliceExp elem) {
		convertNode(elem);
		slicee = Expression.convert(elem.e1);
		from = Expression.convert(elem.lwr);
		to = Expression.convert(elem.upr);
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
