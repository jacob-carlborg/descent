package dtool.dom.definitions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TemplateTypeParameter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.BaseEntityRef;
import dtool.model.IScope;

public class TemplateParamType extends TemplateParameter {

	public BaseEntityRef.TypeConstraint specType;
	public BaseEntityRef.TypeConstraint defaultType;

	public TemplateParamType(TemplateTypeParameter elem) {
		convertNode(elem);
		convertIdentifier(elem.ident);
		this.specType = Entity.convertType(elem.tp_spectype);
		this.defaultType = Entity.convertType(elem.tp_defaulttype);
	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Aggregate;
	}

	/*
	 * Can be null
	 */
	@Override
	public IScope getMembersScope() {
		if(specType == null)
			return null;
		return specType.entity.getTargetScope();
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, defname);
			TreeVisitor.acceptChild(visitor, specType);
			TreeVisitor.acceptChild(visitor, defaultType);
		}
		visitor.endVisit(this);
	}


}
