package dtool.dom.definitions;

import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.Assert;
import melnorme.miscutil.IteratorUtil;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.ast.IASTNode;
import dtool.refmodel.INativeDefUnit;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public abstract class NativeDefUnit extends DefUnit implements INativeDefUnit, IScopeNode {

	public static class NativesScope implements IScope {

		public NativesScope() {
		}

		public Iterator<? extends IASTNode> getMembersIterator() {
			// TODO: put intrinsics here?
			return IteratorUtil.getEMPTY_ITERATOR();
		}

		public IScope getModuleScope() {
			return this;
		}

		public List<IScope> getSuperScopes() {
			return null;
		}
		
		@Override
		public String toString() {
			return "<natives>";
		}
	}
	
	public static final NativesScope nativesScope = new NativesScope();
	
	public NativeDefUnit(String name) {
		setSourceRange(0, 0);
		defname = new Symbol(name);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Native;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		Assert.fail("Intrinsics do not suppport accept.");
	}
	
	@Override
	public String toStringFullSignature() {
		return super.toStringFullSignature();
	}
	
	@Override
	public abstract IScopeNode getMembersScope();
	
	//public abstract IScope getSuperScope();


	public IScope getModuleScope() {
		return nativesScope;
	}

}
