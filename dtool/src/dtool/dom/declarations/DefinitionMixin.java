package dtool.dom.declarations;

import descent.internal.core.dom.TemplateMixin;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.model.IScope;

/*
 * TODO mixin
 */
public class DefinitionMixin extends DefUnit {

	public DefinitionMixin(TemplateMixin elem) {
		super(elem.ident);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Mixin;
	}

	@Override
	public IScope getScope() {
		// TODO Auto-generated method stub
		return null;
	}

}
