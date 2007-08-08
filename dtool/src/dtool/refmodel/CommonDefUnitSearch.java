package dtool.refmodel;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;

import dtool.dom.definitions.DefUnit;

public abstract class CommonDefUnitSearch {

	public static final Collection<DefUnit> NO_DEFUNITS = Collections.emptySet();

	/** Convenience method for wraping a single defunit as a search result. */
	public static Collection<DefUnit> wrapResult(DefUnit defunit) {
		if(defunit == null)
			return null;
		return Collections.singletonList(defunit);
	}
	
	/** Convenience method for extracting a single search result. */
	public static DefUnit getResultDefUnit(Collection<DefUnit> singletonDefunits) {
		return singletonDefunits.iterator().next();
	}

/* ------------------------------ */	
	

	/** Flag for stop searching when suitable matches are found. */
	protected boolean findOnlyOne;
	/** The scope where the reference is located. 
	 * Used for protection access restrictions. */
	protected IScopeNode refScope;

	/** Cached value of the reference's module scope. */
	protected IScope refModuleScope; 
	/** The scopes that have already been searched */
	protected ArrayDeque<IScope> searchedScopes;


	
	public CommonDefUnitSearch(IScopeNode refScope) {
		this.searchedScopes = new ArrayDeque<IScope>(4);
		this.refScope = refScope;
	}
	
	/** Return whether we have already search the given scope or not. */
	public boolean hasSearched(IScope scope) {
		if(searchedScopes.contains(scope))
			return true;
		return false;
	}

	/** Indicate we are now searching the given new scope. */
	public void enterNewScope(IScope scope) {
		// TODO, keep only the named scopes?
		// how about partial scopes?
		searchedScopes.add(scope);
	}
	
	/** Get the Module of the search's reference. */
	public IScope getReferenceModuleScope() {
		if(refModuleScope == null)
			refModuleScope = refScope.getModuleScope();
		return refModuleScope;
	}
	
	/** Return whether the search has found all matches. */
	public abstract boolean isFinished();

	/** Returns whether this search matches the given defUnit or not. */
	public abstract boolean matches(DefUnit defUnit);
	
	/** Adds the matched defunit. */
	public abstract void addMatch(DefUnit defUnit);


}