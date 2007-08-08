package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.NewExp;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Reference;

public class ExpNew extends Expression {

	Expression[] args;
	Expression[] allocargs;
	Reference type;

	public ExpNew(NewExp elem) {
		convertNode(elem);
		this.args = Expression.convertMany(elem.arguments); 
		this.type = Reference.convertType(elem.type);
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
