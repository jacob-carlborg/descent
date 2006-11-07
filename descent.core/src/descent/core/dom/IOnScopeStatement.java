package descent.core.dom;

/**
 * A scope statement:
 * 
 * scope(ident) { }
 */
public interface IOnScopeStatement extends IStatement {
	
	int ON_SCOPE_EXIT = 1;
	int ON_SCOPE_FAILURE = 2;
	int ON_SCOPE_SUCCESS = 3;
	
	/**
	 * Returns the scope type. Check the constants declared
	 * in this interface.
	 */
	int getOnScopeType();
	
	/**
	 * Returns the statement inside this scope.
	 */
	IStatement getStatement();	

}
