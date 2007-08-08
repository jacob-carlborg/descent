package dtool.refmodel;

import java.util.Collection;

import dtool.dom.ast.IASTNode;
import dtool.dom.definitions.DefUnit;

/** A node that references a DefUnit. */
public interface IDefUnitReference extends IASTNode {

	/** Finds the DefUnits matching this reference. 
	 * Result can be null if none found.
	 * XXX: make it allways null for no results? */
	Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly);
}
