package dtool.dom.expressions;

import java.util.Collection;

import dtool.dom.ast.ASTNeoNode;
import dtool.dom.definitions.DefUnit;
import dtool.refmodel.IDefUnitReferenceNode;

public abstract class Resolvable extends ASTNeoNode 
	implements IDefUnitReferenceNode {

	public abstract Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly);
	
	public DefUnit findTargetDefUnit() {
		Collection<DefUnit> defunits = findTargetDefUnits(true);
		if(defunits == null || defunits.isEmpty())
			return null;
		return defunits.iterator().next();
	}
	

}