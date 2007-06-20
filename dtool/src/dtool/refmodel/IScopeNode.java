package dtool.refmodel;

import java.util.List;

import dtool.dom.ast.IASTNode;
import dtool.dom.definitions.DefUnit;

/** 
 * A scope which is an AST node. 
 * Gives access to the scopes DefUnits, and to super scopes. 
 */
public interface IScopeNode extends IASTNode {

	/** Gets all DefUnits in this scope. Must consider direct DefUnit children, 
	 * as well as those in DefUnit container nodes. */
	List<? extends DefUnit> getDefUnits();

	/** Returns the super (as in superclass) scopes of this scope.
	 * FIXME: a scope can be null for now. */
	List<IScopeNode> getSuperScopes();

}
