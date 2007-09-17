package dtool.ast.references;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.NativeDefUnit;
import dtool.ast.expressions.Resolvable;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.IScopeNode;

/**
 * Common class for entity references.
 */
public abstract class Reference extends Resolvable {
	
	public static enum EReferenceConstraint {	
		none,
		type,
		expvalue
	}
	
	public EReferenceConstraint refConstraint = null;
	
	public static IDefUnitReference maybeNullReference(Reference ref) {
		if(ref != null)
			return ref;
		return NativeDefUnit.nullReference;
	}
	
	public IScopeNode getTargetScope() {
		DefUnit defunit = findTargetDefUnit(); 
		if(defunit == null)
			return null;
		return defunit.getMembersScope();
	}
	
	/*public void performSearch(CommonDefUnitSearch search) {
		Collection<DefUnit> defunits = findLookupDefUnits();
		ANeoResolve.doSearchInDefUnits(defunits, search);
	}
	
	public abstract Collection<DefUnit> findLookupDefUnits();
	*/
	
	@Override
	public abstract String toStringAsElement();

	public abstract boolean canMatch(DefUnit defunit);

}

