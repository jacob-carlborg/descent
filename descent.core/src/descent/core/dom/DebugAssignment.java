package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Debug assignment AST node.
 * 
 * <pre>
 * DebugAssignment:
 *     { Modifier } <b>debug</b> <b>=</b> Version
 * </pre>
 */
public class DebugAssignment extends Declaration {

	/**
	 * The "modifiers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor MODIFIERS_PROPERTY =
	internalModifiersPropertyFactory(DebugAssignment.class); //$NON-NLS-1$

	/**
	 * The "version" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor VERSION_PROPERTY =
		new ChildPropertyDescriptor(DebugAssignment.class, "version", Version.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "dDocs" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor D_DOCS_PROPERTY =
	internalDDocsPropertyFactory(DebugAssignment.class); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(3);
		createPropertyList(DebugAssignment.class, properyList);
		addProperty(MODIFIERS_PROPERTY, properyList);
		addProperty(VERSION_PROPERTY, properyList);
		addProperty(D_DOCS_PROPERTY, properyList);
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
	 * The version.
	 */
	private Version version;


	/**
	 * Creates a new unparented debug assignment node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	DebugAssignment(AST ast) {
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
		if (property == D_DOCS_PROPERTY) {
			return dDocs();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

		@Override
		final ChildListPropertyDescriptor internalModifiersProperty() {
			return MODIFIERS_PROPERTY;
		}
		
		@Override
		final ChildListPropertyDescriptor internalDDocsProperty() {
			return D_DOCS_PROPERTY;
		}
		
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return DEBUG_ASSIGNMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		DebugAssignment result = new DebugAssignment(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.modifiers.addAll(ASTNode.copySubtrees(target, modifiers()));
		result.setVersion((Version) getVersion().clone(target));
		result.dDocs.addAll(ASTNode.copySubtrees(target, dDocs()));
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
			acceptChildren(visitor, modifiers);
			acceptChild(visitor, getVersion());
			acceptChildren(visitor, dDocs);
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the version of this debug assignment.
	 * 
	 * @return the version
	 */ 
	public Version getVersion() {
		if (this.version == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.version == null) {
					preLazyInit();
					this.version = new Version(this.ast);
					postLazyInit(this.version, VERSION_PROPERTY);
				}
			}
		}
		return this.version;
	}

	/**
	 * Sets the version of this debug assignment.
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
		if (version == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.version;
		preReplaceChild(oldChild, version, VERSION_PROPERTY);
		this.version = version;
		postReplaceChild(oldChild, version, VERSION_PROPERTY);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 3 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.modifiers.listSize())
			+ (this.version == null ? 0 : getVersion().treeSize())
			+ (this.dDocs.listSize())
	;
	}

}
