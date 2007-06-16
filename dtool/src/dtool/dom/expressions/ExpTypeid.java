package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TypeidExp;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Entity;

public class ExpTypeid extends Expression {

	Entity type;
	
	public ExpTypeid(TypeidExp elem) {
		convertNode(elem);
		this.type = Entity.convertType(elem.type);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}

}
