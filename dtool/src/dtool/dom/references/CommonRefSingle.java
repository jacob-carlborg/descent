package dtool.dom.references;

import java.util.Collection;

import dtool.descentadapter.DescentASTConverter;
import dtool.dom.definitions.DefUnit;
import dtool.refmodel.DefUnitSearch;


/** 
 * Common class for single name references.
 */
public abstract class CommonRefSingle extends Reference {

	public String name;
	
	public static CommonRefSingle convert(descent.internal.core.dom.Identifier elem) {
		return (CommonRefSingle) DescentASTConverter.convertElem(elem);
	}
	
	
	public Collection<DefUnit> findTargetDefUnits(boolean findOneOnly) {
		DefUnitSearch search = new DefUnitSearch(name, this, findOneOnly);
		CommonRefQualified.doSearchForPossiblyQualifiedSingleRef(search, this);
		return search.getDefUnits();
	}

}
