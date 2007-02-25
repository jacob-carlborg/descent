package descent.core.dom;



/**
 * Abstract subclass for expressions.
 * <pre>
 * Expression:
 *    ArrayAccess
 *    ArrayLiteral
 *    AssertExpression
 *    BooleanLiteral
 *    CallExpression
 *    CastExpression
 *    CharacterLiteral
 *    ConditionalExpression
 *    DeleteExpression
 *    DollarLiteral
 *    DotIdentifierExpression
 *    DotTemplateTypeExpression
 *    FileImportExpression
 *    FunctionLiteralDeclarationExpression
 *    InfixExpression
 *    IsTypeExpression
 *    IsTypeSpecializationExpression
 *    MixinExpression
 *    Name
 *    NewAnonymousClassExpression
 *    NewExpression
 *    NullLiteral
 *    NumberLiteral
 *    ParenthesizedExpression
 *    PostfixExpression
 *    PrefixExpression
 *    SliceExpression
 *    StringLiteral
 *    StringsExpression
 *    SuperLiteral
 *    ThisLiteral
 *    TypeExpression
 *    TypeidExpression
 * </pre>
 */
public abstract class Expression extends ASTNode {

	/**
	 * Creates a new AST node for an abstract expression.
	 * <p>
	 * N.B. This constructor is package-private; all subclasses must be 
	 * declared in the same package; clients are unable to declare 
	 * additional subclasses.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	Expression(AST ast) {
		super(ast);
	}
	
	/**
	 * Resolves and returns the binding for the type of this expression.
	 * <p>
	 * Note that bindings are generally unavailable unless requested when the
	 * AST is being built.
	 * </p>
	 * 
	 * @return the binding for the type of this expression, or
	 *    <code>null</code> if the type cannot be resolved
	 */	
	public final ITypeBinding resolveTypeBinding() {
		return this.ast.getBindingResolver().resolveExpressionType(this);
	}
	
}
