package descent.core.dom;

import java.util.List;


/**
 * Abstract subclass for declarations.
 * <pre>
 * Declaration:
 *    AggregateDeclaration
 *    AliasDeclaration
 *    AlignDeclaration
 *    ConditionalDeclaration
 *    DebugAssignment
 *    EnumDeclaration
 *    ExternDeclaration
 *    FunctionDeclaration
 *    ImportDeclaration
 *    InvariantDeclaration
 *    ModifierDeclaration
 *    PragmaDeclaration
 *    StaticAssert
 *    TemplateDeclaration
 *    TemplateMixin
 *    TypedefDeclaration
 *    UnitTestDeclaration
 *    VariableDeclaration
 *    VersionAssignment
 * </pre>
 */
public abstract class Declaration extends ASTNode {

	/**
	 * The modifiers
	 * (element type: <code>Modifier</code>).
	 * Defaults to an empty list.
	 */
	final ASTNode.NodeList modifiers =
		new ASTNode.NodeList(getModifiersProperty());
	
	/**
	 * The documentation comments
	 * (element type: <code>Comment</code>).
	 * Defaults to an empty list.
	 */
	final ASTNode.NodeList dDocs =
		new ASTNode.NodeList(getDDocsProperty());
	
	/**
	 * Returns structural property descriptor for the "modifiers" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildListPropertyDescriptor internalModifiersProperty();
	
	/**
	 * Returns structural property descriptor for the "d docs" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildListPropertyDescriptor internalDDocsProperty();
	
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
	 * Returns structural property descriptor for the "d docs" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final ChildListPropertyDescriptor getDDocsProperty() {
		return internalDDocsProperty();
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "modifiers" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildListPropertyDescriptor internalModifiersPropertyFactory(Class nodeClass) {
		return new ChildListPropertyDescriptor(nodeClass, "modifiers", Modifier.class, NO_CYCLE_RISK); //$NON-NLS-1$
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "d docs" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildListPropertyDescriptor internalDDocsPropertyFactory(Class nodeClass) {
		return new ChildListPropertyDescriptor(nodeClass, "dDocs", Comment.class, NO_CYCLE_RISK); //$NON-NLS-1$
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
	 * declaration.
	 * 
	 * @return the live list of alias declaration
	 *    (element type: <code>Modifier</code>)
	 */ 
	public final List<Modifier> modifiers() {
		return this.modifiers;
	}
	
	/**
	 * Returns the live ordered list of d docs for this
	 * declaration.
	 * 
	 * @return the live list of alias declaration
	 *    (element type: <code>Comment</code>)
	 */ 
	public final List<Comment> dDocs() {
		return this.dDocs;
	}

}
