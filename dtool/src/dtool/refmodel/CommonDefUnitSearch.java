package dtool.refmodel;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;

import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;

public abstract class CommonDefUnitSearch {

	public static final Collection<DefUnit> NO_DEFUNITS = Collections.emptySet();

	/** Convenience method for wraping a single defunit as a search result. */
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
	
	protected boolean findOnlyOne;

	protected Module searchRefModule; //cached value
	protected ArrayDeque<IScope> searchedScopes;

	public CommonDefUnitSearch() {
		this.searchedScopes = new ArrayDeque<IScope>(4);
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
	
	/** Return whether the search has found all matches. */
	public abstract boolean isFinished();

	/** Get the Module of the search's reference. */
	public abstract Module getReferenceModule();

	/** Returns whether this search matches the given defUnit or not. */
	public abstract boolean matches(DefUnit defUnit);
	
	/** */
	public abstract void addResult(DefUnit defunit);


}