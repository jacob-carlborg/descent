package dtool.refmodel;

import java.util.ArrayList;
import java.util.List;

import util.tree.IElement;
import dtool.dom.ast.ASTNode;
import dtool.dom.declarations.DeclarationImport;
import dtool.dom.declarations.DeclarationImport.ImportContent;
import dtool.dom.declarations.DeclarationImport.ImportFragment;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.definitions.DefinitionAggregate.BaseClass;

public class EntityResolver {
	
	protected static IModuleResolver modResolver;

	/** Initializes the EntityResolver with a ModuleResolver. */
	public static void initializeEntityResolver(IModuleResolver modResolver) {
		EntityResolver.modResolver = modResolver;
	}

	
	/* -------- Node Helpers -------- */
	
	/** Gets the DefUnits in the given ASTNode members. 
	 * Considers direct DefUnit instances as well as DefUnit Containers. */
	public static List<DefUnit> getDefUnitsFromMembers(IElement[] members) {
		List<DefUnit> defunits = new ArrayList<DefUnit>();
		for(IElement elem: members) {
			addPossibleDefUnits(elem, defunits);
		}
		return defunits;
	}
	
	/** Gets the DefUnits in the given ASTNode members. 
	 * Considers direct DefUnit instances as well as DefUnit containers. */
	public static List<DefUnit> getDefUnitsFromMembers(List<? extends IElement> members) {
		List<DefUnit> defunits = new ArrayList<DefUnit>();
		for(IElement elem: members) {
			addPossibleDefUnits(elem, defunits);
		}
		return defunits;
	}

	private static void addPossibleDefUnits(IElement elem, List<DefUnit> defunits) {
		if(elem instanceof IDefinitionContainer) {
			ASTNode[] otherMembers = ((IDefinitionContainer) elem).getMembers();
			if(otherMembers != null)
				defunits.addAll(getDefUnitsFromMembers(otherMembers));
		} else if(elem instanceof DefUnit) {
			defunits.add((DefUnit)elem);
		}
	}
	
	/** Finds the first outer scope of the given element, 
	 * navegating through the element's parents. */
	public static IScopeNode getOuterScope(IElement startElem) {
		IElement elem = startElem.getParent();

/*		while(elem != null && (elem instanceof IScope) == false)
			elem = elem.getParent();
	*/	
		while(elem != null) {
			if (elem instanceof IScopeNode)
				return (IScopeNode) elem;
			
			if (elem instanceof BaseClass) {
				// Skip aggregate defunit scope (this is important) 
				elem = elem.getParent().getParent();
				continue;
			}
			
			elem = elem.getParent();
		}
		
		return ((IScopeNode)elem);
	}

	/* --------  -------- */

	/** Searches for the DefUnit with the given name in the given scope,
	 * and then successively in it's outer scopes. */
	public static DefUnit findDefUnitFromSurroundingScope(IScopeNode scope, String name) {
		
		do {
			DefUnit defunit;
			defunit = findDefUnitFromScope(scope, name);
			if(defunit != null)
				return defunit;
			
			// retry in outer scope
			scope = getOuterScope(scope);
		} while (scope != null);

		return null;
	}

	/** Searches for the DefUnit with the given name, 
	 * in the scope's immediate, secondary, and super scopes. */
	public static DefUnit findDefUnitFromScope(IScopeNode scope, String name) {
		DefUnit defunit = findDefUnitInImmediateScope(scope, name);
		if(defunit != null)
			return defunit;
		
		defunit = findDefUnitInSecondaryScope(scope, name);
		if(defunit != null)
			return defunit;

		// Search super scope 
		if(scope.getSuperScopes() != null)
			for(IScopeNode superscope : scope.getSuperScopes()) {
				if(superscope != null)
					defunit = findDefUnitFromScope(superscope, name); 
				if(defunit != null)
					return defunit;
			}
		
		return null;
	}
	

	private static DefUnit findDefUnitInImmediateScope(IScopeNode scope, String name) {
		for (DefUnit defunit : scope.getDefUnits()) {
			if (defunit.defname.equals(name))
				return defunit;
		}
		return null;
	}
	
	private static DefUnit findDefUnitInSecondaryScope(IScopeNode scope, String name) {
		for (IElement elem : scope.getChildren()) {
			if(!(elem instanceof DeclarationImport))
				continue;
				
			DeclarationImport declImport = (DeclarationImport) elem;
			for (ImportFragment impFrag : declImport.imports) {
				return findDefUnitInImportFragment(impFrag, name);
			}

			//declImport.imports
		}
		return null;
	}

	private static DefUnit findDefUnitInImportFragment(ImportFragment impFrag, String name) {
		if(impFrag instanceof ImportContent) {
			ImportContent impMembers = (ImportContent) impFrag;
			Module refModule = NodeUtil.getModule(impMembers);
			String packageName = impMembers.packageEnt.name;
			String moduleName = impMembers.moduleEnt.name;
			Module targetModule;
			targetModule = modResolver.findModule(refModule, packageName, moduleName);
			if(targetModule != null)
				return findDefUnitFromScope(targetModule, name);
		}
		return null;
	}

	/* --------  -------- */

	
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
