package dtool.refmodel;

import java.util.Iterator;

import melnorme.miscutil.IteratorUtil;
import dtool.dom.ast.ASTNode;
import dtool.dom.declarations.DeclarationImport;
import dtool.dom.declarations.ImportContent;
import dtool.dom.declarations.ImportStatic;
import dtool.dom.declarations.PartialPackageDefUnitOfPackage;
import dtool.dom.declarations.DeclarationImport.ImportFragment;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.definitions.Module.DeclarationModule;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * Class with static methods encoding D entity lookup rules.
 * Uses an {@link EntitySearch} during lookups.
 * A scope plus it's outer scopes is called an extended scope.
 */
public class EntityResolver {
	
	protected static IModuleResolver modResolver;

	/** Initializes the EntityResolver with a ModuleResolver. */
	public static void initializeEntityResolver(IModuleResolver modResolver) {
		EntityResolver.modResolver = modResolver;
	}

	/** Convenience method to call mod resolver. */
	public static Module findModule(Module refModule, String packageName, String moduleName) {
		return modResolver.findModule(refModule, packageName, moduleName);
	}
	
	/* ----- Methods for find DefUnits in a collection of ASTNodes ----- */


	
	/* ====================  entity lookup  ==================== */

	/** Searches for DefUnit's with the given name in the given scope,
	 * and then successively in it's outer scopes. 
	 * Uses an {@link EntitySearch} to give search options and store the 
	 * results.
	 * The set of matched {@link DefUnit}s must all be visible in the same
	 * non-extended scope, (altough due to imports, they may originate from 
	 * different scopes XXX: fix this behavior? This is an ambiguity error in D).
	 */
	public static void findDefUnitInExtendedScope(IScopeNode scope,
			EntitySearch search) {

		do {
			findDefUnitInScope(scope, search);
			if(search.isFinished())
				return;

			IScopeNode outerscope = NodeUtil.getOuterScope(scope);
			if(outerscope == null) {
				findDefUnitInModuleDec(scope, search);
				return;
			}

			// retry in outer scope
			scope = outerscope; 
		} while (true);
		
	}

	private static void findDefUnitInModuleDec(IScopeNode scope,
			EntitySearch search) {
		//Module module = NodeUtil.getParentModule((ASTNode)scope);
		Module module = (Module) scope; 
		DeclarationModule decMod = module.md;
		if(decMod != null) {
			DefUnit defUnit;
			
			if(decMod.packages.length == 0 || decMod.packages[0].name == "") {
				defUnit = module;
			} else {
				// Cache this?
			
				String[] packNames = new String[decMod.packages.length];
				for(int i = 0; i< decMod.packages.length; ++i){
					packNames[i] = decMod.packages[i].name;
				}
				
				defUnit = PartialPackageDefUnitOfPackage.createPartialDefUnits(
						packNames, null, module);
			}
			
			if(search.matches(defUnit))
				search.addResult(defUnit);
		}
	}

	/** Searches for the DefUnit with the given name, in the scope's 
	 * immediate namespace, secondary namespace (imports), and super scopes. 
	 * Uses an {@link EntitySearch} to give search options and store the 
	 * results.
	 * Does not search, if the scope has alread been searched in this search.
	 * The set of matched {@link DefUnit}s must all be visible in the same
	 * non-extended scope, (altough due to imports, they may originate from 
	 * different scopes XXX: fix this behavior? This is an ambiguity error in D).
	 */
	public static void findDefUnitInScope(IScope scope, EntitySearch search) {
		if(search.hasSearched(scope))
			return;
		
		search.enterNewScope(scope);
		
		findDefUnitInImmediateScope(scope, search);
		if(search.isFinished())
			return;
		
		findDefUnitInSecondaryScope(scope, search);
		if(search.isFinished())
			return;

		// Search super scope 
		if(scope.getSuperScopes() != null) {
			for(IScope superscope : scope.getSuperScopes()) {
				if(superscope != null)
					findDefUnitInScope(superscope, search); 
				if(search.isFinished())
					return;
			}
		}
		
	}
	

	private static void findDefUnitInImmediateScope(IScope scope, EntitySearch search) {
		Iterator<ASTNode> iter = IteratorUtil.recast(scope.getMembersIterator());
		
		findDefUnits(search, iter);
	}

	private static void findDefUnits(EntitySearch search,
			Iterator<? extends ASTNode> iter) {
		
		while(iter.hasNext()) {
			ASTNode elem = iter.next();

			if(elem instanceof DefUnit) {
				DefUnit defunit = (DefUnit) elem;
				if(search.matches(defunit)) {
					search.addResult(defunit);
					if(search.isFinished() && search.findOnlyOne)
						return; // Return if we only want one match in the scope
				}
			} else if (elem instanceof INonScopedBlock) {
				INonScopedBlock container = ((INonScopedBlock) elem);
				findDefUnits(search, container.getMembersIterator());
				if(search.isFinished() && search.findOnlyOne)
					return; // Return if we only want one match in the scope
			} 
		}
	}
	
	private static void findDefUnitInSecondaryScope(IScope scope, EntitySearch search) {
		Iterator<ASTNode> iter = IteratorUtil.recast(scope.getMembersIterator());
				
		Module thisModule = scope.getModule();
		findSecondaryDefUnits(search, iter, thisModule);
	}

	private static void findSecondaryDefUnits(EntitySearch search,
			Iterator<? extends ASTNode> iter, Module thisModule) {
		
		Module refsModule = search.getReferenceModule();
		
		while(iter.hasNext()) {
			ASTNode elem = iter.next();

			if(elem instanceof DeclarationImport) {
				DeclarationImport declImport = (DeclarationImport) elem;

				// save current searchingFromAnImport value
				boolean searchingFromAnImport = search.searchingFromAnImport;
				
				if(refsModule != thisModule && !declImport.isTransitive)
					continue; // Don't consider private imports
				
				search.searchingFromAnImport = true;
				for (ImportFragment impFrag : declImport.imports) {
					impFrag.searchDefUnit(search);
					// continue regardless of search.findOnlyOne because of partial packages
				}
				// restore previous searchingFromAnImport value
				search.searchingFromAnImport = searchingFromAnImport;

			} else if (elem instanceof INonScopedBlock) {
				INonScopedBlock container = ((INonScopedBlock) elem);
				findSecondaryDefUnits(search, container.getMembersIterator(), thisModule);
			} 

		}
	}
	
	/* ====================  import lookup  ==================== */

	public static void findDefUnitInStaticImport(ImportStatic importStatic, EntitySearch search) {
		DefUnit defunit = importStatic.getDefUnit();
		if(defunit != null && search.matches(defunit))
			search.addResult(defunit);
	}

	public static void findDefUnitInContentImport(ImportContent impContent, EntitySearch search) {
		findDefUnitInStaticImport(impContent, search);
		//if(search.isScopeFinished()) return;

		Module targetModule = findImporTargetModule(impContent);
		if (targetModule != null)
			findDefUnitInScope(targetModule, search);
	}
	
	private static Module findImporTargetModule(ImportFragment impSelective) {
		Module refModule = NodeUtil.getParentModule(impSelective);
		String packageName = impSelective.moduleEnt.packageName;
		String moduleName = impSelective.moduleEnt.moduleName;
		Module targetModule;
		targetModule = findModule(refModule, packageName, moduleName);
		return targetModule;
	}

/*	public static void findDefUnitInSelectiveImport(
			ImportSelective impSelective, EntitySearch search) {

		Module targetModule = findImporTargetModule(impSelective);
		if (targetModule == null)
			return;
			
		for(ASTNode impSelFrag: impSelective.impSelections) {
			if(impSelFrag instanceof ImportSelectiveSelection) {
				EntIdentifier sel;
				sel = ((ImportSelectiveSelection) impSelFrag).targetname;
				if(search.entname.equals(sel.name)) {
					DefUnit defunit = sel.findTargetDefUnit();
					findDefUnitInScope(targetModule, search);
					if(defunit != null)
						search.addResult(defunit);
				}			
			}
		}
	}
*/

}
