package dtool.refmodel;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;

import melnorme.miscutil.Assert;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.declarations.PartialPackageDefUnit;
import dtool.dom.declarations.PartialPackageDefUnitOfModule;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.references.Entity;

public class EntitySearch {
	public static final Collection<DefUnit> NO_DEFUNITS = Collections.emptySet();

	public String entname;
	private Entity searchref;
	private Module searchRefModule; //cached value
	boolean findOnlyOne;

	private ArrayDeque<DefUnit> defunits;
	private ArrayDeque<IScope> searchedScopes;
	private boolean matchesArePartialDefUnits = false;

	//@Deprecated
	public boolean searchingFromAnImport = false;
	

	public static EntitySearch newSearch(String name, Entity searchref) {
		return newSearch(name, searchref, false);
	}
	
	private EntitySearch(String name, Entity searchref, boolean findOneOnly) {
		this.entname = name;
		this.searchref = searchref;
		this.findOnlyOne = findOneOnly;
		//defunits = new ArrayDeque<DefUnit>(4);
		this.searchedScopes = new ArrayDeque<IScope>(4);
	}

	
	public static EntitySearch newSearch(String name, Entity searchref,
			boolean findOneOnly) {
		return new EntitySearch(name, searchref, findOneOnly);
	}

	public static Collection<DefUnit> wrapResult(DefUnit defunit) {
		if(defunit == null)
			return null;
//		ArrayDeque<DefUnit> deque = new ArrayDeque<DefUnit>(1);
//		deque.add(defunit);
		return Collections.singletonList(defunit);
		
	}
	
	/** Convenience method for extracting a single search result. */
	public static DefUnit getResultDefUnit(Collection<DefUnit> singletonDefunits) {
		return singletonDefunits.iterator().next();
	}
	
	public ASTNeoNode getSearchReference() {
		return searchref;
	}
	
	public Module getReferenceModule() {
		if(searchRefModule == null)
			searchRefModule = searchref.getModule();
		return searchRefModule;
	}
	
	public Collection<DefUnit> getDefUnits() {
		return defunits;
	}

	public void addResult(DefUnit defunit) {
		if(defunits == null)
			defunits = new ArrayDeque<DefUnit>(4);
		defunits.add(defunit);
		if(defunit instanceof PartialPackageDefUnit)
			matchesArePartialDefUnits = true;
	}
	
	/** Returns if this search is complete or not. A search is complete when
	 * {@link #findOnlyOne} is set, and it has found all possible valid DefUnits. 
	 * If one match is a partial DefUnit, then the search must continue searching
	 * all scopes, because there could allways be another partial. */
	public boolean isFinished() {
		return defunits != null && !findOnlyOne && !matchesArePartialDefUnits;
	}

	public boolean hasSearched(IScope scope) {
		if(searchedScopes.contains(scope))
			return true;
		return false;
	}

	public void enterNewScope(IScope scope) {
		// TODO, keep only the named scopes?
		// how about partial scopes?
		searchedScopes.add(scope);
	}

	public boolean matches(DefUnit defUnit) {
		return entname.equals(defUnit.getName());
	}


}
