package dtool.model;

import java.util.ArrayList;
import java.util.List;

import util.tree.IElement;
import dtool.dom.ast.ASTNode;
import dtool.dom.base.EntitySingle;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.DefinitionAggregate.BaseClass;

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

		// Search super scope 
		if(scope.getSuperScopes() != null)
			for(IScope superscope : scope.getSuperScopes()) {
				if(superscope != null)
					defunit = getDefUnitFromScope(superscope, name); 
				if(defunit != null)
					return defunit;
			}
		
		return null;
	}
	
	public static DefUnit getDefUnitFromDefUnit(DefUnit root, String name) {
		return getDefUnitFromScope(root.getMembersScope(), name);
	}
	
	public static DefUnit getDefUnitFromSurroundingScope(EntitySingle entity) {
		String name = entity.name;
		
		IScope scope = getOuterScope(entity);
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

/*		while(elem != null && (elem instanceof IScope) == false)
			elem = elem.getParent();
	*/	
		while(elem != null) {
			if (elem instanceof IScope)
				return (IScope) elem;
			
			if (elem instanceof BaseClass) {
				// Skip aggregate defunit scope (this is important) 
				elem = elem.getParent().getParent();
				continue;
			}
			
			elem = elem.getParent();
		}
		
		return ((IScope)elem);
	}

	
	public static List<DefUnit> getDefUnitsFromMembers(IElement[] members) {
		List<DefUnit> defunits = new ArrayList<DefUnit>();
		for(IElement elem: members) {
			addPossibleDefUnits(elem, defunits);
		}
		return defunits;
	}
	
	public static List<DefUnit> getDefUnitsFromMembers(List<? extends IElement> members) {
		List<DefUnit> defunits = new ArrayList<DefUnit>();
		for(IElement elem: members) {
			addPossibleDefUnits(elem, defunits);
		}
		return defunits;
	}

	private static void addPossibleDefUnits(IElement elem, List<DefUnit> defunits) {
		if(elem instanceof DefUnit) {
			defunits.add((DefUnit)elem);
		} else if(elem instanceof IDefinitionContainer) {
			ASTNode[] otherMembers = ((IDefinitionContainer) elem).getMembers();
			if(otherMembers != null)
				defunits.addAll(getDefUnitsFromMembers(otherMembers));
		}
	}

	private static DefUnit findDefUnitInImmediateScope(IScope scope, String name) {
		for (DefUnit defunit : scope.getDefUnits()) {
			if (defunit.defname.equals(name))
				return defunit;
		}
		return null;
	}
	
	private static DefUnit findDefUnitInSecondaryScope(IScope scope, String name) {
		// TODO search imports
		return null;
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
