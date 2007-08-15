package dtool.dom.references;

import java.util.Collection;
import java.util.List;

import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.TemplateInstanceWrapper;
import dtool.dom.definitions.DefUnit;
import dtool.refmodel.DefUnitSearch;


/** 
 * Common class for single name references.
 */
public abstract class CommonRefSingle extends Reference {

	public String name;
	
	public static CommonRefSingle convertToSingleRef(descent.internal.compiler.parser.IdentifierExp elem) {
		if(elem instanceof TemplateInstanceWrapper)
			return new RefTemplateInstance(((TemplateInstanceWrapper) elem).tempinst);
		else
			return elem.ident.equals("") ? null : new RefIdentifier(elem); 
	}
	
	public static void convertManyToRefIdentifier(List<IdentifierExp> idents,
			RefIdentifier[] rets) {
		for(int i = 0; i < idents.size(); ++i) {
			rets[i] = (RefIdentifier) convertToSingleRef(idents.get(i));
		}
	}
	
	public Collection<DefUnit> findTargetDefUnits(boolean findOneOnly) {
		DefUnitSearch search = new DefUnitSearch(name, this, findOneOnly);
		CommonRefQualified.doSearchForPossiblyQualifiedSingleRef(search, this);
		return search.getDefUnits();
	}


}
