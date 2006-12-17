package descent.internal.core.dom;

import descent.core.dom.IStatement;

/**
 * Abstract subclass for statements.
 * <pre>
 * Statement:
 *    AsmStatement
 *    Block
 *    BreakStatement
 *    CaseStatement
 *    ContinueStatement
 *    DebugStatement
 *    DoStatement
 *    ExpressionStatement
 *    ForeachStatement
 *    ForStatement
 *    GotoCaseStatement
 *    GotoDefaultStatement
 *    GotoStatement
 *    IfStatement
 *    IftypeStatement
 *    LabelStatement
 *    PragmaStatement
 *    ReturnStatement
 *    ScopeStatement
 *    StaticAssertStatement
 *    StaticIfStatement
 *    SwitchStatement
 *    SynchronizedStatement
 *    ThrowStatement
 *    TryStatement
 *    VersionStatement
 *    VolatileStatement
 *    WhileStatement
 *    WithStatement
 * </pre>
 */
public abstract class Statement extends ASTNode implements IStatement {
	
	/**
	 * Creates a new AST node for an abstract statement.
	 * <p>
	 * N.B. This constructor is package-private; all subclasses must be 
	 * declared in the same package; clients are unable to declare 
	 * additional subclasses.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	Statement(AST ast) {
		super(ast);
	}
	
}
