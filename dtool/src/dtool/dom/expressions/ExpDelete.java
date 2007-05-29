package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.DeleteExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpDelete extends Expression {

	public Expression exp;
	
	public ExpDelete(DeleteExp elem) {
		convertNode(elem);
		this.exp = Expression.convert(elem.e); 
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);	
	}

}
