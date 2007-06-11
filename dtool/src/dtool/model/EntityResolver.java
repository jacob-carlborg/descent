package dtool.model;

import java.util.ArrayList;
import java.util.List;

import util.tree.IElement;
import dtool.dom.base.EntitySingle;
import dtool.dom.definitions.DefUnit;

public class EntityResolver {
	
	/*public static DefUnit getDefUnit(List<DefUnit> defunits, String name) {
		for (DefUnit defunit : defunits) {
			if (defunit.defname.equals(name))
				return defunit;
		}
		return null;
	}*/

	public static DefUnit getDefUnitFromScope(IScope scope, String name) {
		DefUnit defunit = findDefUnitInImmediateScope(scope, name);
		if(defunit != null)
			return defunit;
		
		defunit = findDefUnitInSecondaryScope(scope, name);
		if(defunit != null)
			return defunit;

		// Search super scope TODO allow mutiple super scopes
		if(scope.getSuperScope() != null)
			return getDefUnitFromScope(scope.getSuperScope(), name);
		
		return null;
	}
	
	public static DefUnit getDefUnitFromDefUnit(DefUnit root, String name) {
		return getDefUnitFromScope(root.getBindingScope(), name);
	}
	
	public static DefUnit getDefUnitFromSurroundingScope(EntitySingle entity) {
		String name = entity.name;
		
		IScope scope = getOuterScope(entity);;
		do {
			DefUnit defunit;
			defunit = getDefUnitFromScope(scope, name);
			if(defunit != null)
				return defunit;
			
			// retry in outer scope
			scope = getOuterScope(scope);
		} while (scope != null);

		return null;
	}

	private static IScope getOuterScope(IElement scope) {
		IElement elem = scope.getParent();
		while(elem != null && (elem instanceof IScope) == false)
			elem = elem.getParent();
		return ((IScope)elem);
	}


	private static DefUnit findDefUnitInImmediateScope(IScope scope, String name) {
		for (DefUnit defunit : scope.getDefUnits()) {
			if (defunit.defname.equals(name))
				return defunit;
		}
		return null;
	}
	
	private static DefUnit findDefUnitInSecondaryScope(IScope scope, String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private static List<DefUnit> findDefUnitsInImmediateScope(IScope scope, String name) {
		List<DefUnit> defunits = new ArrayList<DefUnit>();
		for (DefUnit defunit : scope.getDefUnits()) {
			if (defunit.defname.equals(name))
				defunits.add(defunit);
		}
		return defunits;
	}

	/*
	public DefUnit findEntity(String fqname) throws ModelException {
		String names[] = fqname.split("\\.");
		System.out.println(StringUtil.collToString(names, " . ") );
		
		IScope scopeent = Main.testdproj;
		//DefUnit defunit;
		if((scopeent instanceof DefUnit) == false) {
			throw new NotADefUnitModelException();
		}
		return (DefUnit) scopeent;

	}*/

}
