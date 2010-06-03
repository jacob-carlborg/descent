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
 *    DeclarationStatement
 *    DefaultStatement
 *    DoStatement
 *    EmptyStatement
 *    ExpressionStatement
 *    ForeachRangeStatement
 *    ForeachStatement    
 *    ForStatement
 *    GotoCaseStatement
 *    GotoDefaultStatement
 *    GotoStatement
 *    LabeledStatement
 *    PragmaStatement
 *    ReturnStatement
 *    ScopeStatement
 *    StaticAssertStatement
 *    SwitchCase
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
