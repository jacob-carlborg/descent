package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.NewExp;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;

public class ExpNew extends Expression {

	Expression[] args;
	Expression[] allocargs;
	Entity type;

	public ExpNew(NewExp elem) {
		convertNode(elem);
		this.args = Expression.convertMany(elem.arguments); 
		this.type = Entity.convertType(elem.type);
		this.allocargs = null; // TODO allocargs
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, allocargs);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);
	}

}
