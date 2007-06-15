package dtool.model;

import java.util.List;

import dtool.dom.ast.IASTNode;
import dtool.dom.definitions.DefUnit;

/** 
 * A scope in the D language. A scope is a set of declarations and statements. 
 */
public interface IScope extends IASTNode {

	/** Gets DefUnits of this scope. */
	List<DefUnit> getDefUnits();

	/** Returns the super (as in superclass) scopes of this scope.
	 * FIXME: a scope can be null for now. */
	List<IScope> getSuperScopes();

}
