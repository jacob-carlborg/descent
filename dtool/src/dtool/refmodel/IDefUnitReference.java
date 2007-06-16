package dtool.refmodel;

import dtool.dom.ast.IASTNode;
import dtool.dom.definitions.DefUnit;

/** A node that references a DefUnit. */
public interface IDefUnitReference extends IASTNode {

	/** Finds the DefUnit referenced by this reference, or null if none found. */
	public DefUnit getTargetDefUnit();
	
}
