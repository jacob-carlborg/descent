package dtool.refmodel;

import java.util.Iterator;
import java.util.List;

import dtool.dom.ast.IASTNode;

/**
 * Gives access to the scope's DefUnits, and to super scopes. 
 */
public interface IScope {


	/** Gets all members of this scope, DefUnit or not. 
	 * Used to iterate and find DefUnits .*/
	Iterator<? extends IASTNode> getMembersIterator();
	
	/** Returns the super (as in superclass) scopes of this scope.
	 * Scopes should be ordered according to priority.
	 * FIXME: a scope can be null for now. */
	List<IScope> getSuperScopes();
	
	/** Gets the module of scope. TODO: Define if it can be null */
	IScope getModuleScope();
	
}