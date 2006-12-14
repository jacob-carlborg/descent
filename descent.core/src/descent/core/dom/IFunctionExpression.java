package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Argument;
import descent.internal.core.dom.FunctionLiteralDeclarationExpression.Syntax;

/**
 * 
 * @author Ary
 *
 */
public interface IFunctionExpression extends IExpression {
	
	Syntax getSyntax();
	
	/**
	 * Returns the arguments of the function.
	 */
	List<Argument> arguments();

	/**
	 * Determines whether this function is variadic.
	 */
	boolean isVariadic();
	
	/**
	 * Returns the body of the function.
	 */
	IStatement getBody();
	
	/**
	 * Returns the precondition of the function, if any, or <code>null</code>.
	 */
	IStatement getPrecondition();
	
	/**
	 * Returns the postcondition of the function, if any, or <code>null</code>.
	 */
	IStatement getPostcondition();
	
	/**
	 * Returns the name of the out clase of the function, if any, or <code>null</code>.
	 */
	ISimpleName getPostconditionVariableName();

}
