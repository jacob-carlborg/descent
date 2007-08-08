package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.CastExp;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Reference;

public class ExpCast extends Expression {
	
	Expression exp;
	Reference type;

	public ExpCast(CastExp elem) {
		convertNode(elem);
		this.exp = Expression.convert(elem.e); 
		this.type = Reference.convertType(elem.t);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}

}
