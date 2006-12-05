package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Argument;
import descent.internal.core.dom.TemplateParameter;
import descent.internal.core.dom.FunctionDeclaration.Kind;

/**
 * A generic function declaration. It can be a function, constructor, destructor,
 * static constructor, static destructor, new or delete functions.
 * 
 * <p>Note that if the aggregate is templated (i.e. <code>foo(T)(...) { }</code>) the parser
 * dosen't generate a template declaration: instead, it makes this class templated.</p>
 */
public interface IFunctionDeclaration extends IDeclaration, IModifiersContainer {
	
	int FUNCTION = 1;
	int CONSTRUCTOR = 2;
	int DESTRUCTOR = 3;
	int STATIC_CONSTRUCTOR = 4;
	int STATIC_DESTRUCTOR = 5;
	int NEW = 6;
	int DELETE = 7;
	
	/**
	 * Returns the type of this function. Check the constants defined
	 * in this interface.
	 */
	Kind getKind();
	
	/**
	 * Returns the name of the function. Note that for constructors
	 * the name is "this", for destructors is "~this", for new is "new"
	 * and for delete is "delete".
	 */
	ISimpleName getName();
	
	/**
	 * Returns the return type of this function. May be <code>null</code>
	 * for constructors, for example.
	 */
	IType getReturnType();
	
	/**
	 * Returns the arguments of the function.
	 */
	List<Argument> arguments();

	/**
	 * Determines whether this function is variadic.
	 */
	boolean isVariadic();
	
	/**
	 * Returns the template parameters. Pre: isTemplate().
	 */
	List<TemplateParameter> templateParameters();
	
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
