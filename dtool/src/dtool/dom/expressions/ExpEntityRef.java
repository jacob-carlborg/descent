package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.IdentifierExp;
import descent.internal.core.dom.TypeDotIdExp;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.EntityConstrainedRef;
import dtool.dom.base.EntitySingle;

public class ExpEntityRef extends Expression {
	
	public EntityConstrainedRef.ValueConstraint entity;
	
	public ExpEntityRef(IdentifierExp element) {
		setSourceRange(element);
		Entity entity = (Entity) DescentASTConverter.convertElem(element.id);
		this.entity = new EntityConstrainedRef.ValueConstraint(entity);
	}
	
	public ExpEntityRef(TypeDotIdExp element) {
		setSourceRange(element);
		Entity.QualifiedEnt qent = new Entity.QualifiedEnt();
		qent.topent = (Entity) DescentASTConverter.convertElem(element.t);
		qent.baseent = EntitySingle.convert(element.ident);
		this.entity = new EntityConstrainedRef.ValueConstraint(qent);
	}
	


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, entity);
		}
		visitor.endVisit(this);	 
	}

}
