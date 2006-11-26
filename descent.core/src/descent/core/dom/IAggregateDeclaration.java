package descent.core.dom;

/**
 * <p>Represents an aggregate declaration, such as a class, interface, struct or union.</p>
 * 
 * <p>Note that if the aggregate is templated (i.e. <code>class Foo(T) { }</code>) the parser
 * dosen't generate a template declaration: instead, it makes this class templated.</p>
 */
public interface IAggregateDeclaration extends IDeclaration, IModifiersContainer, ICommented {
	
	/**
	 * Constant representing a class declaration. 
	 */
	int CLASS_DECLARATION = 1;
	
	/**
	 * Constant representing an interface declaration. 
	 */
	int INTERFACE_DECLARATION = 2;
	
	/**
	 * Constant representing a struct declaration. 
	 */
	int STRUCT_DECLARATION = 3;
	
	/**
	 * Constant representing a union declaration. 
	 */
	int UNION_DECLARATION = 4;
	
	/**
	 * Determines which one of this is this aggregate: class, interface,
	 * struct or union, according to the constants defines in this interface.
	 */
	int getAggregateDeclarationType();
	
	/**
	 * Returns the name of this aggregate. May be <code>null</code> if this
	 * is annonymous.
	 */
	IName getName();
	
	/**
	 * Returns the base classes of this aggregate. May be empty but
	 * never <code>null</code>.
	 */
	IBaseClass[] getBaseClasses();
	
	/**
	 * Returns the declaration definitions contained in this aggregate.
	 * May be empty but never <code>null</null>.
	 */
	IDeclaration[] getDeclarationDefinitions();
	
	/**
	 * Determines if this aggregate is templated.
	 */
	boolean isTemplate();
	
	/**
	 * Returns the template parameters. Pre: isTemplate().
	 */
	ITemplateParameter[] getTemplateParameters();

}
