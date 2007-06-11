package dtool.model;

import util.Assert;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;

public abstract class IntrinsicDefUnit extends DefUnit implements IIntrinsicUnit, IScope {

	@Override
	public EArcheType getArcheType() {
		return null;
	}

	@Override
	public IScope getBindingScope() {
		return this;
	}
	
	public IScope getSuperScope() {
		return null;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		Assert.fail("Intrinsics do not suppport accept.");
	}

}
