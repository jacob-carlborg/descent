package dtool.ast.references;

import static melnorme.miscutil.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.TemplateInstanceWrapper;
import dtool.ast.definitions.DefUnit;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.ReferenceResolver;


/** 
 * Common class for single name fragment references that can be the subref
 * of qualified references.
 */
public abstract class CommonRefSingle extends NamedReference {

	public String name;
	
	public static CommonRefSingle convertToSingleRef(descent.internal.compiler.parser.IdentifierExp elem
			, ASTConversionContext convContext) {
		if(elem instanceof TemplateInstanceWrapper)
			return new RefTemplateInstance(((TemplateInstanceWrapper) elem).tempinst, convContext);
		else
			return elem.ident.length == 0 ? null : new RefIdentifier(elem); 
	}
	
	public static void convertManyToRefIdentifier(List<IdentifierExp> idents, RefIdentifier[] rets
			, ASTConversionContext convContext) {
		for(int i = 0; i < idents.size(); ++i) {
			rets[i] = (RefIdentifier) convertToSingleRef(idents.get(i), convContext);
		}
	}
	
	@Override
	protected String getReferenceName() {
		return name;
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findOneOnly) {
		DefUnitSearch search = new DefUnitSearch(name, this, this.getOffset(), findOneOnly);
		CommonRefSingle.doSearchForPossiblyQualifiedSingleRef(search, this);
		return search.getMatchDefUnits();
	}
	
	@Override
	public void doSearch(PrefixDefUnitSearch search) {
		CommonRefSingle.doSearchForPossiblyQualifiedSingleRef(search, this);
	}

	/** Does a search determining the correct lookup scope when
	 * the CommonRefSingle is part of a qualified referencet. */
	public static void doSearchForPossiblyQualifiedSingleRef(CommonDefUnitSearch search,
			CommonRefSingle refSingle) {
		// First determine the lookup scope.
		if(refSingle.getParent() instanceof CommonRefQualified) {
			CommonRefQualified parent = (CommonRefQualified) refSingle.getParent();
			// check if this single ref is the sub ref of a qualified ref
			if(parent.getSubRef() == refSingle) {
				// then we must do qualified search (use root as the lookup scopes)
				CommonRefQualified.doQualifiedSearch(search, parent);
				return;
			} else {
				assertTrue(parent.getRoot() == refSingle);
				// continue using outer scope as the lookup
			}
		}

		IScopeNode lookupScope = ReferenceResolver.getStartingScope(refSingle);
		ReferenceResolver.findDefUnitInExtendedScope(lookupScope, search);
	}

}
