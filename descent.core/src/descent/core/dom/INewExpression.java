package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Expression;

/**
 * A new expression:
 * 
 * <pre>
 * new type(arg1, arg2, ..., argN)
 * </pre>
 */
public interface INewExpression extends IExpression {
	
	/**
	 * Returns the type to create.
	 */
	IType getType();
	
	/**
	 * Returns the arguments of the expression.
	 */
	List<Expression> constructorArguments();

}
