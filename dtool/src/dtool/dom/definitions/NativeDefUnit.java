package dtool.dom.definitions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.Assert;
import melnorme.miscutil.IteratorUtil;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.refmodel.IDefUnitReference;
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

		@Override
		public String toStringAsElement() {
			return toString();
		}
	}
	
	public static final NativesScope nativesScope = new NativesScope();
	//public static final DefUnit unknown = new NativesScope();
	public static final IDefUnitReference nullReference = new IDefUnitReference() {

		public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
			return null;
		}
		
		@Override
		public String toString() {
			return "<unknown>";
		}
	};

	public NativeDefUnit(String name) {
		super(new Symbol(name));
		setSourceRange(0, 0);
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
	public String toStringForHoverSignature() {
		return super.toStringForHoverSignature();
	}
	
	@Override
	public abstract IScopeNode getMembersScope();
	
	//public abstract IScope getSuperScope();


	public IScope getModuleScope() {
		return nativesScope;
	}

}
