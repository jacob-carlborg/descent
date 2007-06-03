package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.IdentifierExp;
import descent.internal.core.dom.ScopeExp;
import descent.internal.core.dom.TypeDotIdExp;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.EntityConstrainedRef;
import dtool.dom.base.EntitySingle;

public class ExpEntityRef extends Expression {
	
	public EntityConstrainedRef.ValueConstraint entity;
	
	public ExpEntityRef(IdentifierExp elem) {
		convertNode(elem);
		Entity entity = (Entity) DescentASTConverter.convertElem(elem.id);
		this.entity = new EntityConstrainedRef.ValueConstraint(entity);
	}
	
	public ExpEntityRef(TypeDotIdExp elem) {
		convertNode(elem);
		Entity.QualifiedEnt qent = new Entity.QualifiedEnt();
		qent.topent = (Entity) DescentASTConverter.convertElem(elem.t);
		qent.baseent = EntitySingle.convert(elem.ident);
		this.entity = new EntityConstrainedRef.ValueConstraint(qent);
	}
	
	public ExpEntityRef(ScopeExp elem) {
		convertNode(elem);
		Entity entity = (Entity) DescentASTConverter.convertElem(elem.tempinst);
		this.entity = new EntityConstrainedRef.ValueConstraint(entity);
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
