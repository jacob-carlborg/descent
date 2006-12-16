package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IDebugDeclaration;

/**
 * Debug declaration AST node type.
 *
 * <pre>
 * DebugDeclaration:
 *    { Modifier } <b>debug</b> [ <b>(</b> Version <b>)</b> ] { Declaration } [ <b>else</b> { Declaration } ]
 * </pre>
 */
public class DebugDeclaration extends Declaration implements IDebugDeclaration {
	
	/**
	 * The "modifiers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor MODIFIERS_PROPERTY =
	internalModifiersPropertyFactory(DebugDeclaration.class); //$NON-NLS-1$

	/**
	 * The "version" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor VERSION_PROPERTY =
		new ChildPropertyDescriptor(DebugDeclaration.class, "version", Version.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "thenDeclarations" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor THEN_DECLARATIONS_PROPERTY =
		new ChildListPropertyDescriptor(DebugDeclaration.class, "thenDeclarations", Declaration.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "elseDeclarations" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor ELSE_DECLARATIONS_PROPERTY =
		new ChildListPropertyDescriptor(DebugDeclaration.class, "elseDeclarations", Declaration.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(4);
		createPropertyList(DebugDeclaration.class, properyList);
		addProperty(MODIFIERS_PROPERTY, properyList);
		addProperty(VERSION_PROPERTY, properyList);
		addProperty(THEN_DECLARATIONS_PROPERTY, properyList);
		addProperty(ELSE_DECLARATIONS_PROPERTY, properyList);
		PROPERTY_DESCRIPTORS = reapPropertyList(properyList);
	}

	/**
	 * Returns a list of structural property descriptors for this node type.
	 * Clients must not modify the result.
	 * 
	 * @param apiLevel the API level; one of the
	 * <code>AST.JLS*</code> constants

	 * @return a list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor})
	 * @since 3.0
	 */
	public static List propertyDescriptors(int apiLevel) {
		return PROPERTY_DESCRIPTORS;
	}

	/**
	 * The modifiers
	 * (element type: <code>Modifier</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList modifiers =
		new ASTNode.NodeList(MODIFIERS_PROPERTY);
	/**
	 * The version.
	 */
	private Version version;

	/**
	 * The then declarations
	 * (element type: <code>Declaration</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList thenDeclarations =
		new ASTNode.NodeList(THEN_DECLARATIONS_PROPERTY);
	/**
	 * The else declarations
	 * (element type: <code>Declaration</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList elseDeclarations =
		new ASTNode.NodeList(ELSE_DECLARATIONS_PROPERTY);

	/**
	 * Creates a new unparented debug declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	DebugDeclaration(AST ast) {
		super(ast);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final List internalStructuralPropertiesForType(int apiLevel) {
		return propertyDescriptors(apiLevel);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == VERSION_PROPERTY) {
			if (get) {
				return getVersion();
			} else {
				setVersion((Version) child);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetChildProperty(property, get, child);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final List internalGetChildListProperty(ChildListPropertyDescriptor property) {
		if (property == MODIFIERS_PROPERTY) {
			return modifiers();
		}
		if (property == THEN_DECLARATIONS_PROPERTY) {
			return thenDeclarations();
		}
		if (property == ELSE_DECLARATIONS_PROPERTY) {
			return elseDeclarations();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

		@Override
		final ChildListPropertyDescriptor internalModifiersProperty() {
			return MODIFIERS_PROPERTY;
		}
		
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return DEBUG_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		DebugDeclaration result = new DebugDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.modifiers.addAll(ASTNode.copySubtrees(target, modifiers()));
	result.setVersion((Version) ASTNode.copySubtree(target, getVersion()));
		result.thenDeclarations.addAll(ASTNode.copySubtrees(target, thenDeclarations()));
		result.elseDeclarations.addAll(ASTNode.copySubtrees(target, elseDeclarations()));
		return result;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final boolean subtreeMatch0(ASTMatcher matcher, Object other) {
		// dispatch to correct overloaded match method
		return matcher.match(this, other);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	void accept0(ASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if (visitChildren) {
			// visit children in normal left to right reading order
			acceptChildren(visitor, modifiers());
			acceptChild(visitor, getVersion());
			acceptChildren(visitor, thenDeclarations());
			acceptChildren(visitor, elseDeclarations());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the version of this debug declaration.
	 * 
	 * @return the version
	 */ 
	public Version getVersion() {
		return this.version;
	}

	/**
	 * Sets the version of this debug declaration.
	 * 
	 * @param version the version
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setVersion(Version version) {
		ASTNode oldChild = this.version;
		preReplaceChild(oldChild, version, VERSION_PROPERTY);
		this.version = version;
		postReplaceChild(oldChild, version, VERSION_PROPERTY);
	}

	/**
	 * Returns the live ordered list of then declarations for this
	 * debug declaration.
	 * 
	 * @return the live list of debug declaration
	 *    (element type: <code>Declaration</code>)
	 */ 
	public List<Declaration> thenDeclarations() {
		return this.thenDeclarations;
	}

	/**
	 * Returns the live ordered list of else declarations for this
	 * debug declaration.
	 * 
	 * @return the live list of debug declaration
	 *    (element type: <code>Declaration</code>)
	 */ 
	public List<Declaration> elseDeclarations() {
		return this.elseDeclarations;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 4 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.modifiers.listSize())
			+ (this.version == null ? 0 : getVersion().treeSize())
			+ (this.thenDeclarations.listSize())
			+ (this.elseDeclarations.listSize())
	;
	}

}
