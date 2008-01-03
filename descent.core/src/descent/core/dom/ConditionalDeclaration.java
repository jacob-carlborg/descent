package descent.core.dom;

import java.util.List;


/**
 * Abstract subclass for conditional declarations.
 * 
 * ConditionalDeclaration:
 *    DebugDeclaration
 *    IftypeDeclaration
 *    StaticIfDeclaration
 *    VersionDeclaration
 */
public abstract class ConditionalDeclaration extends Declaration {
	
	/**
	 * The then declarations
	 * (element type: <code>Declaration</code>).
	 * Defaults to an empty list.
	 */
	final ASTNode.NodeList thenDeclarations =
		new ASTNode.NodeList(getThenDeclarationsProperty());
	
	/**
	 * The else declarations
	 * (element type: <code>Declaration</code>).
	 * Defaults to an empty list.
	 */
	final ASTNode.NodeList elseDeclarations =
		new ASTNode.NodeList(getElseDeclarationsProperty());
	
	/**
	 * Returns structural property descriptor for the "thenDeclarations" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildListPropertyDescriptor internalThenDeclarationsProperty();
	
	/**
	 * Returns structural property descriptor for the "elseDeclarations" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildListPropertyDescriptor internalElseDeclarationsProperty();
	
	/**
	 * Returns structural property descriptor for the "thenDeclarations" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final ChildListPropertyDescriptor getThenDeclarationsProperty() {
		return internalThenDeclarationsProperty();
	}
	
	/**
	 * Returns structural property descriptor for the "elseDeclarations" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final ChildListPropertyDescriptor getElseDeclarationsProperty() {
		return internalElseDeclarationsProperty();
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "thenDeclarations" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildListPropertyDescriptor internalThenDeclarationsPropertyFactory(Class nodeClass) {
		return new ChildListPropertyDescriptor(nodeClass, "thenDeclarations", Declaration.class, CYCLE_RISK); //$NON-NLS-1$
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "elseDeclarations" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildListPropertyDescriptor internalElseDeclarationsPropertyFactory(Class nodeClass) {
		return new ChildListPropertyDescriptor(nodeClass, "elseDeclarations", Declaration.class, CYCLE_RISK); //$NON-NLS-1$
	}
	
	/**
	 * Creates a new AST node for an abstract conditional declaration.
	 * <p>
	 * N.B. This constructor is package-private; all subclasses must be 
	 * declared in the same package; clients are unable to declare 
	 * additional subclasses.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ConditionalDeclaration(AST ast) {
		super(ast);
	}
	
	/**
	 * Returns the live ordered list of then declarations for this
	 * declaration.
	 * 
	 * @return the live list of declaration
	 *    (element type: <code>Declaration</code>)
	 */ 
	public List<Declaration> thenDeclarations() {
		return this.thenDeclarations;
	}

	/**
	 * Returns the live ordered list of else declarations for this
	 * declaration.
	 * 
	 * @return the live list of declaration
	 *    (element type: <code>Declaration</code>)
	 */ 
	public List<Declaration> elseDeclarations() {
		return this.elseDeclarations;
	}
	
	/**
	 * Determines if the condition of this declaration is active. 
	 * This return {@link Boolean#TRUE}
	 * if it's active, {@link Boolean#FALSE} if it is not active,
	 * or <code>null</code> if it is unknown, or bindings are not available. 
	 * @return true, false or null if condiiton of this declaration is
	 * active, not active, or if it is unknown or bindings are not available. 
	 */
	public final Boolean isActive() {
		return ast.getBindingResolver().resolveConditionalDeclaration(this);
	}

}
