package dtool.dom.declarations;

import java.util.List;

import melnorme.miscutil.ArrayUtil;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.definitions.Symbol;
import dtool.dom.references.RefModule;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * A kinda fake DefUnit, for partial package "definitions" of imports. 
 * This partial DefUnit holds one DefUnit DefUnit and represents only 
 * part of it's complete namespace.
 */
public abstract class PartialPackageDefUnit extends DefUnit implements IScopeNode {

	public PartialPackageDefUnit(Symbol name) {
		super(name);
	}

	public static PartialPackageDefUnit createPartialDefUnits(
			String[] packages, RefModule entModule, Module module) {
		Symbol defname = new Symbol(packages[0]);
		if(packages.length == 1 ) {
			PartialPackageDefUnitOfModule packageDefUnit =  new PartialPackageDefUnitOfModule(defname);
			packageDefUnit.module = module;
			packageDefUnit.moduleRef = entModule;
			return packageDefUnit;
		} else {
			PartialPackageDefUnitOfPackage packageDefUnit =  new PartialPackageDefUnitOfPackage(defname);
			String[] newNames = ArrayUtil.copyOfRange(packages, 1, packages.length);
			packageDefUnit.child = createPartialDefUnits(newNames, entModule, null);
			return packageDefUnit;
		}
	}

	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Package;
	}
	
	@Override
	public String toStringAsCodeCompletion() {
		return getName();
	}
	
	@Override
	public String toStringFullSignature() {
		return getName();
	}
	

	@Override
	public void accept0(IASTNeoVisitor visitor) {
	}
	
	@Override
	public Module getModuleScope() {
		return null;
	}
	
	@Override
	public IScopeNode getMembersScope() {
		return this;
	}
	
	public List<IScope> getSuperScopes() {
		return null;
	}
	
}