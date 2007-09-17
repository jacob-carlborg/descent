package dtool.refmodel;

import java.util.ArrayList;
import java.util.Collection;

import dtool.ast.declarations.PartialPackageDefUnit;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Resolvable;

/**
 * Normal DefUnit search, 
 * searches for DefUnit's whose defname matches the search name. 
 */
public class DefUnitSearch extends CommonDefUnitSearch {

	protected String searchName;

	private ArrayList<DefUnit> defunits;
	protected boolean matchesArePartialDefUnits = false;

	public DefUnitSearch(String name, Resolvable searchref) {
		this(name, searchref, false);
	}
	
	public DefUnitSearch(String searchName, Resolvable searchref, boolean findOneOnly) {
		super(NodeUtil.getOuterScope(searchref));
		this.searchName = searchName;
		this.findOnlyOne = findOneOnly;
		//defunits = new ArrayDeque<DefUnit>(4);
	}
	

	
	public Collection<DefUnit> getDefUnits() {
		return defunits;
	}

	@Override
	public void addMatch(DefUnit defunit) {
		if(defunits == null)
			defunits = new ArrayList<DefUnit>(4);
		defunits.add(defunit);
		if(defunit instanceof PartialPackageDefUnit)
			matchesArePartialDefUnits = true;
	}
	
	/** Returns if this search is complete or not. A search is complete when
	 * {@link #findOnlyOne} is set, and it has found all possible valid DefUnits. 
	 * If one match is a partial DefUnit, then the search must continue searching
	 * all scopes, because there could allways be another partial. */
	@Override
	public boolean isFinished() {
		return defunits != null && !matchesArePartialDefUnits;
	}

	@Override
	public boolean matches(DefUnit defUnit) {
		return matchesName(defUnit.getName());
	}
	
	@Override
	public boolean matchesName(String defName) {
		return searchName.equals(defName);
	}

}
