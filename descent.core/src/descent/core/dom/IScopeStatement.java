package descent.core.dom;

import descent.internal.core.dom.ScopeStatement.Event;

/**
 * A scope statement:
 * 
 * scope(ident) { }
 */
public interface IScopeStatement extends IStatement {
	
	Event getEvent();
	
	/**
	 * Returns the statement inside this scope.
	 */
	IStatement getBody();

}
