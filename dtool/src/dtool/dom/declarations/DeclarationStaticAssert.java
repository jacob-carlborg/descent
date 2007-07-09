package dtool.dom.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.StaticAssert;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;

public class DeclarationStaticAssert extends ASTNeoNode {

	public Expression pred;
	public Expression msg;
	
	public DeclarationStaticAssert(StaticAssert elem) {
		convertNode(elem);
		this.pred = Expression.convert(elem.exp);
		this.msg = Expression.convert(elem.msg);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, pred);
			TreeVisitor.acceptChildren(visitor, msg);
		}
		visitor.endVisit(this);
	}

}
