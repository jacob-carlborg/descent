package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.DotIdExp;
import descent.internal.core.dom.IdentifierExp;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.EntitySingle;

public class ExpDotEntityRef extends Expression {

	public Expression exp;
	public EntitySingle entity;

	public ExpDotEntityRef(DotIdExp element) {
		setSourceRange(element);
		this.exp = Expression.convert(element.e);
		this.entity = EntitySingle.convert(element.id);
	}
	
	public Entity convertRef(IdentifierExp exp) {
		return EntitySingle.convert(exp.id);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, entity);
		}
		visitor.endVisit(this);	 
	}

}
