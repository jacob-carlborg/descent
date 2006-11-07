package descent.core.dom;

/**
 * A statement of the D language.
 */
public interface IStatement extends IDElement {
	
	/**
	 * Constant representing an expression statement.
	 * A statement with this type can be safely cast to <code>IExpressionStatement</code>. 
	 */
	int STATEMENT_EXPRESSION = 1;
	
	/**
	 * Constant representing a declaration statement.
	 * A statement with this type can be safely cast to <code>IDeclarationStatement</code>. 
	 */
	int STATEMENT_DECLARATION = 2;
	
	/**
	 * Constant representing a break statement.
	 * A statement with this type can be safely cast to <code>IBreakStatement</code>. 
	 */
	int STATEMENT_BREAK = 3;
	
	/**
	 * Constant representing a continue statement.
	 * A statement with this type can be safely cast to <code>IContinueStatement</code>. 
	 */
	int STATEMENT_CONTINUE = 4;
	
	/**
	 * Constant representing a return statement.
	 * A statement with this type can be safely cast to <code>IReturnStatement</code>. 
	 */
	int STATEMENT_RETURN = 5;
	
	/**
	 * Constant representing a while statement.
	 * A statement with this type can be safely cast to <code>IWhileStatement</code>. 
	 */
	int STATEMENT_WHILE = 5;
	
	/**
	 * Constant representing a do while statement.
	 * A statement with this type can be safely cast to <code>IDoWhileStatement</code>. 
	 */
	int STATEMENT_DO_WHILE = 6;
	
	/**
	 * Constant representing a label statement.
	 * A statement with this type can be safely cast to <code>ILabelStatement</code>. 
	 */
	int STATEMENT_LABEL = 7;
	
	/**
	 * Constant representing a static assert statement.
	 * A statement with this type can be safely cast to <code>IStaticAssertStatement</code>. 
	 */
	int STATEMENT_STATIC_ASSERT = 8;
	
	/**
	 * Constant representing a with statement.
	 * A statement with this type can be safely cast to <code>IWithStatement</code>. 
	 */
	int STATEMENT_WITH = 9;
	
	/**
	 * Constant representing a for statement.
	 * A statement with this type can be safely cast to <code>IForStatement</code>. 
	 */
	int STATEMENT_FOR = 10;
	
	/**
	 * Constant representing a foreach or foreach_reverse statement.
	 * A statement with this type can be safely cast to <code>IForeachStatement</code>. 
	 */
	int STATEMENT_FOREACH = 11;
	
	/**
	 * Constant representing a volatile statement.
	 * A statement with this type can be safely cast to <code>IVolatileStatement</code>. 
	 */
	int STATEMENT_VOLATILE = 11;
	
	/**
	 * Constant representing a switch statement.
	 * A statement with this type can be safely cast to <code>ISwitchStatement</code>. 
	 */
	int STATEMENT_SWITCH = 12;
	
	/**
	 * Constant representing a case statement.
	 * A statement with this type can be safely cast to <code>ICaseStatement</code>. 
	 */
	int STATEMENT_CASE = 13;
	
	/**
	 * Constant representing a scope statement.
	 * A statement with this type can be safely cast to <code>IScopeStatement</code>. 
	 */
	int STATEMENT_SCOPE = 14;
	
	/**
	 * Constant representing a compound statement.
	 * A statement with this type can be safely cast to <code>ICompoundStatement</code>. 
	 */
	int STATEMENT_COMPOUND = 15;
	
	/**
	 * Constant representing a try statement.
	 * A statement with this type can be safely cast to <code>ITryStatement</code>. 
	 */
	int STATEMENT_TRY = 16;
	
	/**
	 * Constant representing a throw statement.
	 * A statement with this type can be safely cast to <code>IThrowStatement</code>. 
	 */
	int STATEMENT_THROW = 17;
	
	/**
	 * Constant representing a synchronized statement.
	 * A statement with this type can be safely cast to <code>ISynchronizedStatement</code>. 
	 */
	int STATEMENT_SYNCHRONIZED = 18;
	
	/**
	 * Constant representing a scope statement.
	 * A statement with this type can be safely cast to <code>IScopeStatement</code>. 
	 */
	int STATEMENT_ON_SCOPE = 19;
	
	/**
	 * Constant representing a goto statement.
	 * A statement with this type can be safely cast to <code>IGotoStatement</code>. 
	 */
	int STATEMENT_GOTO = 20;
	
	/**
	 * Constant representing a goto default statement. 
	 */
	int STATEMENT_GOTO_DEFAULT = 21;
	
	/**
	 * Constant representing a goto case statement.
	 * A statement with this type can be safely cast to <code>IGotoCaseStatement</code>. 
	 */
	int STATEMENT_GOTO_CASE = 22;
	
	/**
	 * Constant representing a default statement.
	 * A statement with this type can be safely cast to <code>IDefaultStatement</code>. 
	 */
	int STATEMENT_DEFAULT = 23;
	
	/**
	 * Constant representing a pragma statement.
	 * A statement with this type can be safely cast to <code>IPragmaStatement</code>. 
	 */
	int STATEMENT_PRAGMA = 24;
	
	/**
	 * Constant representing an if statement.
	 * A statement with this type can be safely cast to <code>IIfStatement</code>. 
	 */
	int STATEMENT_IF = 25;
	
	/**
	 * Constant representing a conditional statement.
	 * A statement with this type can be safely cast to <code>IConditionalStatement</code>. 
	 */
	int STATEMENT_CONDITIONAL = 26;
	
	/**
	 * Constant representing an asm statement.
	 * A statement with this type can be safely cast to <code>IAsmStatement</code>. 
	 */
	int STATEMENT_ASM = 27;
	
	/**
	 * Return the kind of this statement. Check the constants
	 * defined in this interface.
	 */
	int getStatementType();

}