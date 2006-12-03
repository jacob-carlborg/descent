package descent.core.dom;

/**
 * A boolean expression.
 * 
 * <pre>
 * true | false
 * </pre>
 */
public interface IBooleanLiteral extends IExpression {
	
	boolean booleanValue();

}
