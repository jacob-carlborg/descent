package dtool.dom.definitions;

import descent.internal.core.dom.TemplateMixin;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.model.IScope;

/*
 * TODO mixin
 */
public class DefinitionMixin extends DefUnit {

	public DefinitionMixin(TemplateMixin elem) {
		convertDsymbol(elem);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Mixin;
	}

	@Override
	public IScope getScope() {
		return null;
	}

}
