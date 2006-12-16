package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDeclaration;

public abstract class Declaration extends Dsymbol implements IDeclaration {

	/**
	 * The modifiers
	 * (element type: <code>Modifier</code>).
	 * Defaults to an empty list.
	 */
	final ASTNode.NodeList modifiers =
		new ASTNode.NodeList(getModifiersProperty());
	
	/**
	 * Returns structural property descriptor for the "modifiers" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildListPropertyDescriptor internalModifiersProperty();
	
	/**
	 * Returns structural property descriptor for the "componentType" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final ChildListPropertyDescriptor getModifiersProperty() {
		return internalModifiersProperty();
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "name" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildListPropertyDescriptor internalModifiersPropertyFactory(Class nodeClass) {
		return new ChildListPropertyDescriptor(nodeClass, "modifiers", Modifier.class, NO_CYCLE_RISK); //$NON-NLS-1$
	}
	
	public int storage_class;
	
	public Declaration() {
	}
	
	public Declaration(Identifier id) {
		this.ident = id;
	}
	
	/**
	 * Creates a new AST node for an abstract declaration.
	 * <p>
	 * N.B. This constructor is package-private; all subclasses must be 
	 * declared in the same package; clients are unable to declare 
	 * additional subclasses.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	Declaration(AST ast) {
		super(ast);
	}
	
	/**
	 * Returns the live ordered list of modifiers for this
	 * alias declaration.
	 * 
	 * @return the live list of alias declaration
	 *    (element type: <code>Modifier</code>)
	 */ 
	public final List<Modifier> modifiers() {
		return this.modifiers;
	}

}
