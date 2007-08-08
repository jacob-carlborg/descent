package dtool.refmodel;

import java.util.Iterator;

import melnorme.miscutil.ExceptionAdapter;
import melnorme.miscutil.IteratorUtil;

import org.eclipse.core.runtime.CoreException;

import dtool.dom.ast.IASTNode;
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
 * Uses an {@link DefUnitSearch} during lookups.
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
		try {
			return modResolver.findModule(refModule, packageName, moduleName);
		} catch (CoreException ce) {
			throw ExceptionAdapter.unchecked(ce);
		}
	}
	
	/* ----- Methods for find DefUnits in a collection of ASTNodes ----- */


	
	/* ====================  entity lookup  ==================== */

	/** Searches for DefUnit's with the given name in the given scope,
	 * and then successively in it's outer scopes. 
	 * Uses an {@link DefUnitSearch} to give search options and store the 
	 * results.
	 * The set of matched {@link DefUnit}s must all be visible in the same
	 * non-extended scope, (altough due to imports, they may originate from 
	 * different scopes XXX: fix this behavior? This is an ambiguity error in D).
	 */
	public static void findDefUnitInExtendedScope(IScopeNode scope,
			CommonDefUnitSearch search) {

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
			CommonDefUnitSearch search) {
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
				search.addMatch(defUnit);
		}
	}

	/** Searches for the DefUnit with the given name, in the scope's 
	 * immediate namespace, secondary namespace (imports), and super scopes. 
	 * Uses an {@link DefUnitSearch} to give search options and store the 
	 * results.
	 * Does not search, if the scope has alread been searched in this search.
	 * The set of matched {@link DefUnit}s must all be visible in the same
	 * non-extended scope, (altough due to imports, they may originate from 
	 * different scopes XXX: fix this behavior? This is an ambiguity error in D).
	 */
	public static void findDefUnitInScope(IScope scope, CommonDefUnitSearch search) {
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
	

	private static void findDefUnitInImmediateScope(IScope scope, CommonDefUnitSearch search) {
		Iterator<IASTNode> iter = IteratorUtil.recast(scope.getMembersIterator());
		
		findDefUnits(search, iter);
	}

	private static void findDefUnits(CommonDefUnitSearch search,
			Iterator<? extends IASTNode> iter) {
		
		while(iter.hasNext()) {
			IASTNode elem = iter.next();

			if(elem instanceof DefUnit) {
				DefUnit defunit = (DefUnit) elem;
				if(search.matches(defunit)) {
					search.addMatch(defunit);
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
	
	private static void findDefUnitInSecondaryScope(IScope scope, CommonDefUnitSearch search) {
		Iterator<IASTNode> iter = IteratorUtil.recast(scope.getMembersIterator());
				
		IScope thisModule = scope.getModuleScope();
		findSecondaryDefUnits(search, iter, thisModule);
	}

	private static void findSecondaryDefUnits(CommonDefUnitSearch search,
			Iterator<? extends IASTNode> iter, IScope thisModule) {
		
		IScope refsModule = search.getReferenceModuleScope();
		
		while(iter.hasNext()) {
			IASTNode elem = iter.next();

			if(elem instanceof DeclarationImport) {
				DeclarationImport declImport = (DeclarationImport) elem;

				if(!refsModule.equals(thisModule) && !declImport.isTransitive)
					continue; // Don't consider private imports
				
				for (ImportFragment impFrag : declImport.imports) {
					impFrag.searchDefUnit(search);
					// continue regardless of search.findOnlyOne because of partial packages
				}

			} else if (elem instanceof INonScopedBlock) {
				INonScopedBlock container = ((INonScopedBlock) elem);
				findSecondaryDefUnits(search, container.getMembersIterator(), thisModule);
			} 

		}
	}
	
	/* ====================  import lookup  ==================== */

	public static void findDefUnitInStaticImport(ImportStatic importStatic, CommonDefUnitSearch search) {
		DefUnit defunit = importStatic.getDefUnit();
		if(defunit != null && search.matches(defunit))
			search.addMatch(defunit);
	}

	public static void findDefUnitInContentImport(ImportContent impContent, CommonDefUnitSearch search) {
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
