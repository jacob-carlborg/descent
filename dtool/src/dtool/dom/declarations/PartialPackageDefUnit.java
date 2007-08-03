package dtool.dom.declarations;

import java.util.Arrays;
import java.util.List;

import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.definitions.Symbol;
import dtool.dom.references.EntModule;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * A kinda fake DefUnit, for partial package "definitions" of imports. 
 * This partial DefUnit holds one DefUnit DefUnit and represents only 
 * part of it's complete namespace.
 */
public abstract class PartialPackageDefUnit extends DefUnit implements IScopeNode {

	
	public static PartialPackageDefUnit createPartialDefUnits(
			String[] packages, EntModule entModule, Module module) {
		if(packages.length == 1 ) {
			PartialPackageDefUnitOfModule packageDefUnit =  new PartialPackageDefUnitOfModule();
			packageDefUnit.defname = new Symbol(packages[0]);
			packageDefUnit.module = module;
			packageDefUnit.entModule = entModule;
			return packageDefUnit;
		} else {
			PartialPackageDefUnitOfPackage packageDefUnit =  new PartialPackageDefUnitOfPackage();
			packageDefUnit.defname = new Symbol(packages[0]);
			String[] newNames = Arrays.copyOfRange(packages, 1, packages.length);
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
	public Module getModule() {
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