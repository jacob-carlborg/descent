package descent.internal.core.dom;

import descent.core.dom.IInitializer;

/**
 * Abstract subclass for initializers.
 * <pre>
 * Initializer:
 *    ArrayInitializer
 *    ExpressionInitializer
 *    StructInitializer
 *    VoidInitializer
 * </pre>
 */
public abstract class Initializer extends ASTNode implements IInitializer {

	/**
	 * Creates a new AST node for an abstract initializer.
	 * <p>
	 * N.B. This constructor is package-private; all subclasses must be 
	 * declared in the same package; clients are unable to declare 
	 * additional subclasses.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	Initializer(AST ast) {
		super(ast);
	}
	
}
