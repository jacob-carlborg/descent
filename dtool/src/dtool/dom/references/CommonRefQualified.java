package dtool.dom.references;

import java.util.Collection;

import melnorme.miscutil.Assert;

import dtool.dom.definitions.DefUnit;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.IDefUnitReferenceNode;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.NodeUtil;


/**
 * Common class for qualified references 
 * There are two: normal qualified references and Module qualified references.
 */
public abstract class CommonRefQualified extends Reference implements IDefUnitReferenceNode {

	
	public CommonRefSingle subref;

	/** Return the sub-reference (the reference on the right side). */
	public CommonRefSingle getSubRef() {
		return subref;
	}
	
	/** maybe null */
	public abstract IDefUnitReferenceNode getRoot();

	public abstract Collection<DefUnit> findRootDefUnits();
	
	/** Finds the target defunits of this qualified reference. */
	public Collection<DefUnit> findTargetDefUnits(boolean findOneOnly) {
		DefUnitSearch search = new DefUnitSearch(subref.name, this);
		doQualifiedSearch(search, this);
		return search.getDefUnits();
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
				doQualifiedSearch(search, parent);
				return;
			} else {
				Assert.isTrue(parent.getRoot() == refSingle);
				// continue using outer scope as the lookup
			}
		}
		IScopeNode lookupScope = NodeUtil.getOuterScope(refSingle);
		EntityResolver.findDefUnitInExtendedScope(lookupScope, search);
	}

	public static void doQualifiedSearch(CommonDefUnitSearch search, CommonRefQualified qref) {
		Collection<DefUnit> defunits = qref.findRootDefUnits();
		findDefUnitInScopes(defunits, search);
	}


	private static void findDefUnitInScopes(
			Collection<DefUnit> defunits, CommonDefUnitSearch search) {
		if(defunits == null)
			return;
		
		for (DefUnit unit : defunits) {
			IScopeNode scope = unit.getMembersScope();
			EntityResolver.findDefUnitInScope(scope, search);
			if(search.isFinished())
				return;
		}
	}
	
}
