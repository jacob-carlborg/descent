package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.TypeidExp;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Reference;

public class ExpTypeid extends Expression {

	Reference type;
	
	public ExpTypeid(TypeidExp elem) {
		convertNode(elem);
		this.type = Reference.convertType(elem.type);
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
