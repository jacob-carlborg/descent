package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.DotIdExp;
import descent.internal.core.dom.IdentifierExp;
import descent.internal.core.dom.ScopeExp;
import descent.internal.core.dom.TypeDotIdExp;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.EntQualified;
import dtool.dom.base.Entity;
import dtool.dom.base.EntitySingle;
import dtool.dom.definitions.DefUnit;

public class ExpEntity extends Expression {
	
	public Entity entity;
	
	public ExpEntity(IdentifierExp elem) {
		convertNode(elem);
		this.entity = (Entity) DescentASTConverter.convertElem(elem.id);
		//this.baseEntity = new BaseEntityRef.ValueConstraint(entity);
	}
	
	public ExpEntity(TypeDotIdExp elem) {
		convertNode(elem);
		EntQualified qent = new EntQualified();
		qent.root = (Entity) DescentASTConverter.convertElem(elem.t);
		qent.subent = EntitySingle.convert(elem.ident);
		qent.startPos = qent.getRootExp().startPos;
		qent.setEndPos(qent.subent.getEndPos());
		this.entity = qent;
		//this.baseEntity = new BaseEntityRef.ValueConstraint(qent);
	}
	
	public ExpEntity(ScopeExp elem) {
		convertNode(elem);
		this.entity = (Entity) DescentASTConverter.convertElem(elem.tempinst);
	}
	

	public ExpEntity(DotIdExp elem) {
		convertNode(elem);
		this.entity = EntQualified.convertDotIexp(elem);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, entity);
		}
		visitor.endVisit(this);	 
	}
	
	@Override
	public DefUnit getTargetDefUnit() {
		return entity.getTargetDefUnit();
	}

}
