package descent.internal.core.dom;

import descent.core.dom.IExpression;

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
 *    FunctionLiteralDeclarationExpression
 *    InfixExpression
 *    IsTypeExpression
 *    IsTypeSpecializationExpression
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
public abstract class Expression extends ASTNode implements IExpression {

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
	
}
