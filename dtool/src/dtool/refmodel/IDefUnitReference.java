package dtool.refmodel;

import java.util.Collection;

import dtool.dom.definitions.DefUnit;

/** A reference to a DefUnit. */
public interface IDefUnitReference {

	/** Finds the DefUnits matching this reference. 
	 * Result can be null if none found.
	 * XXX: make it allways null for no results? */
	Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly);
	
	String toStringAsElement();
}
