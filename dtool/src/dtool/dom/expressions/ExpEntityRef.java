package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.IdentifierExp;
import descent.internal.core.dom.ScopeExp;
import descent.internal.core.dom.TypeDotIdExp;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.EntQualified;
import dtool.dom.base.Entity;
import dtool.dom.base.EntitySingle;

public class ExpEntityRef extends Expression {
	
	public Entity entity;

	
	public ExpEntityRef(IdentifierExp elem) {
		convertNode(elem);
		this.entity = (Entity) DescentASTConverter.convertElem(elem.id);
		//this.baseEntity = new BaseEntityRef.ValueConstraint(entity);
	}
	
	public ExpEntityRef(TypeDotIdExp elem) {
		convertNode(elem);
		EntQualified qent = new EntQualified();
		qent.topent = (Entity) DescentASTConverter.convertElem(elem.t);
		qent.baseent = EntitySingle.convert(elem.ident);
		qent.startPos = qent.topent.startPos;
		qent.setEndPos(qent.baseent.getEndPos());
		this.entity = qent;
		//this.baseEntity = new BaseEntityRef.ValueConstraint(qent);
	}
	
	public ExpEntityRef(ScopeExp elem) {
		convertNode(elem);
		this.entity = (Entity) DescentASTConverter.convertElem(elem.tempinst);
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
