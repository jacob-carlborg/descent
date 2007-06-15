package dtool.dom.definitions;

import descent.internal.core.dom.TemplateMixin;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.BaseEntityRef;
import dtool.model.IScope;

/*
 * TODO mixin
 */
public class DefinitionMixin extends DefUnit  {
	
	public BaseEntityRef.TypeConstraint type;
	
	public DefinitionMixin(TemplateMixin elem) {
		convertDsymbol(elem);
		//this.type = Entity.convertType(elem.qName);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Mixin;
	}

	@Override
	public IScope getMembersScope() {
		return type.entity.getTargetScope();
	}

}
