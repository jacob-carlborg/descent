package descent.core.dom;



/**
 * Abstract subclass for statements.
 * <pre>
 * Statement:
 *    AsmStatement
 *    Block
 *    BreakStatement
 *    CaseStatement
 *    ConditionalStatement
 *    ContinueStatement
 *    DoStatement
 *    ExpressionStatement
 *    ForeachStatement
 *    ForStatement
 *    GotoCaseStatement
 *    GotoDefaultStatement
 *    GotoStatement
 *    IfStatement
 *    LabelStatement
 *    PragmaStatement
 *    ReturnStatement
 *    ScopeStatement
 *    StaticAssertStatement
 *    SwitchStatement
 *    SynchronizedStatement
 *    ThrowStatement
 *    TryStatement
 *    VolatileStatement
 *    WhileStatement
 *    WithStatement
 * </pre>
 */
public abstract class Statement extends ASTNode {
	
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
