package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.SliceExp;
import descent.internal.compiler.parser.TypeSlice;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Reference;
import dtool.refmodel.IDefUnitReference;

public class ExpSlice extends Expression {

	public IDefUnitReference slicee;
	public Expression from;
	public Expression to;
	
	public ExpSlice(SliceExp elem) {
		convertNode(elem);
		slicee = Expression.convert(elem.e1);
		from = Expression.convert(elem.lwr);
		to = Expression.convert(elem.upr);
	}
	
	public ExpSlice(TypeSlice elem) {
		slicee = Reference.convertType(elem.next);
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
