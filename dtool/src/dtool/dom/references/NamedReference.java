package dtool.dom.references;

import dtool.refmodel.PrefixDefUnitSearch;

/** 
 * A reference based on an identifier. These references also 
 * allow doing a search based on their lookup rules.
 */
public abstract class NamedReference extends Reference {
 
	/** Perform a search using the lookup rules of this reference. */
	public abstract void doSearch(PrefixDefUnitSearch search);


}
