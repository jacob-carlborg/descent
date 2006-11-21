package descent.core.dom;

/**
 * An expression in the D language.
 */
public interface IExpression extends IDElement {
	
	/**
	 * Constant representing a this expression.
	 */
	int EXPRESSION_THIS = 1;
	
	/**
	 * Constant representing a super expression.
	 */
	int EXPRESSION_SUPER = 2;
	
	/**
	 * Constant representing a null expression.
	 */
	int EXPRESSION_NULL = 3;
	
	/**
	 * Constant representing a true expression.
	 */
	int EXPRESSION_TRUE = 4;
	
	/**
	 * Constant representing a false expression.
	 */
	int EXPRESSION_FALSE = 5;
	
	/**
	 * Constant representing a string expression.
	 * An expression with this type can be safely cast to <code>IStringExpression</code>. 
	 */
	int EXPRESSION_STRING = 6;
	
	/**
	 * Constant representing an integer expression.
	 * An expression with this type can be safely cast to <code>IIntegerExpression</code>. 
	 */
	int EXPRESSION_INTEGER = 7;
	
	/**
	 * Constant representing a real expression.
	 * TODO: An expression with this type can be safely cast to <code>IStringExpression</code>. 
	 */
	int EXPRESSION_REAL = 8;
	
	/**
	 * Constant representing an assert expression.
	 * An expression with this type can be safely cast to <code>IAssertExpression</code>. 
	 */
	int EXPRESSION_ASSERT = 9;
	
	/**
	 * Constant representing a parenthesized expression.
	 * An expression with this type can be safely cast to <code>IParenthesizedExpression</code>. 
	 */
	int EXPRESSION_PARENTHESIZED = 10;
	
	/**
	 * Constant representing a binary expression.
	 * An expression with this type can be safely cast to <code>IBinaryExpression</code>. 
	 */
	int EXPRESSION_BINARY = 11;
	
	/**
	 * Constant representing a condition expression.
	 * An expression with this type can be safely cast to <code>IConditionExpression</code>. 
	 */
	int EXPRESSION_CONDITION = 12;
	
	/**
	 * Constant representing an identifier expression. <code>toString()</code>
	 * returns the string of the identifier.
	 */
	int EXPRESSION_IDENTIFIER = 13;
	
	/**
	 * Constant representing a type dot identifier expression.
	 * An expression with this type can be safely cast to <code>ITypeDotIdentifierExpression</code>. 
	 */
	int EXPRESSION_TYPE_DOT_IDENTIFIER = 14;
	
	/**
	 * Constant representing a delete expression.
	 * An expression with this type can be safely cast to <code>IDeleteExpression</code>. 
	 */
	int EXPRESSION_DELETE = 15;
	
	/**
	 * Constant representing a unary expression.
	 * An expression with this type can be safely cast to <code>IUnaryExpression</code>. 
	 */
	int EXPRESSION_UNARY = 16;
	
	/**
	 * Constant representing a cast expression.
	 * An expression with this type can be safely cast to <code>ICastExpression</code>. 
	 */
	int EXPRESSION_CAST = 17;
	
	/**
	 * Constant representing a scope expression.
	 * An expression with this type can be safely cast to <code>IScopeExpression</code>. 
	 */
	int EXPRESSION_SCOPE = 18;
	
	/**
	 * Constant representing a new expression.
	 * An expression with this type can be safely cast to <code>INewExpression</code>. 
	 */
	int EXPRESSION_NEW = 19;
	
	/**
	 * Constant representing an array expression.
	 * An expression with this type can be safely cast to <code>IArrayExpression</code>. 
	 */
	int EXPRESSION_ARRAY = 20;
	
	/**
	 * Constant representing an array literal expression.
	 * An expression with this type can be safely cast to <code>IArrayLiteralExpression</code>. 
	 */
	int EXPRESSION_ARRAY_LITERAL = 21;
	
	/**
	 * Constant representing a slice expression.
	 * An expression with this type can be safely cast to <code>ISliceExpression</code>. 
	 */
	int EXPRESSION_SLICE = 22;
	
	/**
	 * Constant representing a dollar expression. 
	 */
	int EXPRESSION_DOLAR = 23;
	
	/**
	 * Constant representing a call expression.
	 * An expression with this type can be safely cast to <code>ICallExpression</code>. 
	 */
	int EXPRESSION_CALL = 24;
	
	/**
	 * Constant representing a type expression.
	 * An expression with this type can be safely cast to <code>ITypeExpression</code>. 
	 */
	int EXPRESSION_TYPE = 25;
	
	/**
	 * Constant representing a dot id expression.
	 * An expression with this type can be safely cast to <code>IDotIdExpression</code>. 
	 */
	int EXPRESSION_DOT_ID = 26;

	/**
	 * Constant representing a typeid expression.
	 * An expression with this type can be safely cast to <code>ITypeidExpression</code>. 
	 */
	int EXPRESSION_TYPEID = 27;

	/**
	 * Constant representing an is expression.
	 * An expression with this type can be safely cast to <code>IIsExpression</code>. 
	 */
	int EXPRESSION_IFTYPE = 28;
	
	/**
	 * Constant representing a new anonymous class expression.
	 * An expression with this type can be safely cast to <code>INewAnonymousClassExpression</code>. 
	 */
	int EXPRESSION_NEW_ANONYMOUS_CLASS = 29;
	
	/**
	 * Constant representing a function expression.
	 * An expression with this type can be safely cast to <code>IFunctionExpression</code>. 
	 */
	int EXPRESSION_FUNCTION = 20;
	
	/**
	 * Returns the type of this expression. Check the constants
	 * defined in this interface.
	 */
	int getExpressionType();

}
