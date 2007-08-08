package dtool.dom.expressions;

import java.util.Collection;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.DotIdExp;
import descent.internal.core.dom.IdentifierExp;
import descent.internal.core.dom.ScopeExp;
import descent.internal.core.dom.TypeDotIdExp;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.references.RefQualified;
import dtool.dom.references.Reference;
import dtool.dom.references.CommonRefSingle;

public class ExpEntity extends Expression {
	
	public Reference entity;
	
	public ExpEntity(IdentifierExp elem) {
		convertNode(elem);
		this.entity = (Reference) DescentASTConverter.convertElem(elem.id);
		//this.baseEntity = new BaseEntityRef.ValueConstraint(entity);
	}
	
	public ExpEntity(TypeDotIdExp elem) {
		convertNode(elem);
		RefQualified qent = new RefQualified();
		qent.root = (Reference) DescentASTConverter.convertElem(elem.t);
		qent.subref = CommonRefSingle.convert(elem.ident);
		qent.startPos = qent.getRootAsNode().startPos;
		qent.setEndPos(qent.subref.getEndPos());
		this.entity = qent;
		//this.baseEntity = new BaseEntityRef.ValueConstraint(qent);
	}
	
	public ExpEntity(ScopeExp elem) {
		convertNode(elem);
		this.entity = (Reference) DescentASTConverter.convertElem(elem.tempinst);
	}
	

	public ExpEntity(DotIdExp elem) {
		convertNode(elem);
		this.entity = RefQualified.convertDotIexp(elem);
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
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return entity.findTargetDefUnits(findFirstOnly);
	}

}
