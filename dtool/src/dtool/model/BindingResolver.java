package dtool.model;

import java.util.ArrayList;
import java.util.List;

import util.StringUtil;
import util.tree.IElement;
import dtool.Main;
import dtool.dom.definitions.DefUnit;

public class BindingResolver {
	
	/*public static DefUnit getDefUnit(List<DefUnit> defunits, String name) {
		for (DefUnit defunit : defunits) {
			if (defunit.defname.equals(name))
				return defunit;
		}
		return null;
	}*/


	public static DefUnit getDefUnit(IScope scope, String name) {
		
		do {
			DefUnit defunit = findDefUnitInImmediateScope(scope, name);
			if(defunit != null)
				return defunit;

			defunit = findDefUnitInImmediateSecondaryScope(scope, name);
			if(defunit != null)
				return defunit;

			// retry in upper scope level
			scope = getParentScope(scope);
		} while (scope != null);

		return null;
	}

	private static IScope getParentScope(IScope scope) {
		IElement elem = scope;
		while(elem != null || (elem instanceof IScope) == false)
			elem = elem.getParent();
		return ((IScope)elem);
	}

	private static DefUnit findDefUnitInImmediateSecondaryScope(IScope scope, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	private static DefUnit findDefUnitInImmediateScope(IScope scope, String name) {
		for (DefUnit defunit : scope.getDefUnits()) {
			if (defunit.defname.equals(name))
				return defunit;
		}
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
	
	
	public DefUnit findEntity(String fqname) throws ModelException {
		String names[] = fqname.split("\\.");
		System.out.println(StringUtil.collToString(names, " . ") );
		
		IScope scopeent = Main.testdproj;
		//DefUnit defunit;
		if((scopeent instanceof DefUnit) == false) {
			throw new NotADefUnitModelException();
		}
		return (DefUnit) scopeent;

	}


}
