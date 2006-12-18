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
 *    DotTemplateInstanceExp TODO
 *    FunctionLiteralDeclarationExpression
 *    InfixExpression
 *    IsTypeExpression
 *    IsTypeSpecializationExpression
 *    Name
 *    NewAnonClassExp TODO
 *    NewExpression TODO
 *    NullLiteral
 *    NumberLiteral
 *    ParenthesizedExpression
 *    PostfixExpression
 *    PrefixExpression
 *    ScopeExp TODO
 *    SliceExpression
 *    StringLiteral
 *    StringsExpression
 *    SuperLiteral
 *    ThisLiteral
 *    TypeDotIdExp TODO
 *    TypeExp TODO
 *    TypeidExp TODO
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
