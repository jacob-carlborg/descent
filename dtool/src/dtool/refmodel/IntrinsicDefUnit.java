package dtool.refmodel;

import melnorme.miscutil.Assert;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;

public abstract class IntrinsicDefUnit extends DefUnit implements IIntrinsicUnit, IScopeNode {

	public IntrinsicDefUnit() {
		setSourceRange(0, 0);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Aggregate;
	}

	@Override
	public abstract IScopeNode getMembersScope();
	
	//public abstract IScope getSuperScope();


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		Assert.fail("Intrinsics do not suppport accept.");
	}
	
	@Override
	public String toString() {
		return "<intrinsic>";
	}

}
