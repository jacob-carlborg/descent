package descent.core.dom;

import descent.internal.core.dom.TemplateParameter;

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
	int getFunctionDeclarationType();
	
	/**
	 * Returns the name of the function. Note that for constructors
	 * the name is "this", for destructors is "~this", for new is "new"
	 * and for delete is "delete".
	 */
	IName getName();
	
	/**
	 * Returns the return type of this function. May be <code>null</code>
	 * for constructors, for example.
	 */
	IType getReturnType();
	
	/**
	 * Returns the arguments of the function.
	 */
	IArgument[] getArguments();

	/**
	 * Determines whether this function is variadic.
	 */
	boolean isVariadic();
	
	/**
	 * Determines whether this function is a template.
	 */
	boolean isTemplate();
	
	/**
	 * Returns the template parameters. Pre: isTemplate().
	 */
	TemplateParameter[] getTemplateParameters();
	
	/**
	 * Returns the body of the function.
	 */
	IStatement getBody();
	
	/**
	 * Returns the precondition of the function, if any, or <code>null</code>.
	 */
	IStatement getIn();
	
	/**
	 * Returns the postcondition of the function, if any, or <code>null</code>.
	 */
	IStatement getOut();
	
	/**
	 * Returns the name of the out clase of the function, if any, or <code>null</code>.
	 */
	IName getOutName();

}
