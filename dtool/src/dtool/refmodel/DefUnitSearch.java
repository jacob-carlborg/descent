package dtool.refmodel;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;

import dtool.dom.declarations.PartialPackageDefUnit;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.references.Entity;

public class DefUnitSearch extends CommonDefUnitSearch {

	protected String searchName;
	protected Entity searchRef;

	private ArrayDeque<DefUnit> defunits;
	protected boolean matchesArePartialDefUnits = false;

	public DefUnitSearch(String name, Entity searchref) {
		this(name, searchref, false);
	}
	
	public DefUnitSearch(String searchName, Entity searchref, boolean findOneOnly) {
		this.searchName = searchName;
		this.searchRef = searchref;
		this.findOnlyOne = findOneOnly;
		//defunits = new ArrayDeque<DefUnit>(4);
	}
	
	public Module getReferenceModule() {
		if(searchRefModule == null)
			searchRefModule = searchRef.getModule();
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
		return defunits != null && !matchesArePartialDefUnits;
	}

	public boolean matches(DefUnit defUnit) {
		return searchName.equals(defUnit.getName());
	}

}