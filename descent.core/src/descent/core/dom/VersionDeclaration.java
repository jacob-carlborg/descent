package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Version declaration AST node type.
 *
 * <pre>
 * VersionDeclaration:
 *    <b>version</b> [ <b>(</b> name <b>)</b> ] { Declaration } [ <b>else</b> { Declaration } ]
 * </pre>
 */
public class VersionDeclaration extends ConditionalDeclaration {
	
	/**
	 * The "modifiers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor MODIFIERS_PROPERTY =
	internalModifiersPropertyFactory(VersionDeclaration.class); //$NON-NLS-1$

	/**
	 * The "version" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor VERSION_PROPERTY =
		new ChildPropertyDescriptor(VersionDeclaration.class, "version", Version.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "thenDeclarations" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor THEN_DECLARATIONS_PROPERTY =
	internalThenDeclarationsPropertyFactory(VersionDeclaration.class); //$NON-NLS-1$

	/**
	 * The "elseDeclarations" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor ELSE_DECLARATIONS_PROPERTY =
	internalElseDeclarationsPropertyFactory(VersionDeclaration.class); //$NON-NLS-1$

	/**
	 * The "dDocs" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor D_DOCS_PROPERTY =
	internalDDocsPropertyFactory(VersionDeclaration.class); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(5);
		createPropertyList(VersionDeclaration.class, properyList);
		addProperty(MODIFIERS_PROPERTY, properyList);
		addProperty(VERSION_PROPERTY, properyList);
		addProperty(THEN_DECLARATIONS_PROPERTY, properyList);
		addProperty(ELSE_DECLARATIONS_PROPERTY, properyList);
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
	 * Creates a new unparented version declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	VersionDeclaration(AST ast) {
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
		final ChildListPropertyDescriptor internalThenDeclarationsProperty() {
			return THEN_DECLARATIONS_PROPERTY;
		}
		
		@Override
		final ChildListPropertyDescriptor internalElseDeclarationsProperty() {
			return ELSE_DECLARATIONS_PROPERTY;
		}
		
		@Override
		final ChildListPropertyDescriptor internalDDocsProperty() {
			return D_DOCS_PROPERTY;
		}
		
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return VERSION_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		VersionDeclaration result = new VersionDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.modifiers.addAll(ASTNode.copySubtrees(target, modifiers()));
	result.setVersion((Version) ASTNode.copySubtree(target, getVersion()));
		result.thenDeclarations.addAll(ASTNode.copySubtrees(target, thenDeclarations()));
		result.elseDeclarations.addAll(ASTNode.copySubtrees(target, elseDeclarations()));
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
			acceptChildren(visitor, thenDeclarations);
			acceptChildren(visitor, elseDeclarations);
			acceptChildren(visitor, dDocs);
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the version of this version declaration.
	 * 
	 * @return the version
	 */ 
	public Version getVersion() {
		return this.version;
	}

	/**
	 * Sets the version of this version declaration.
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

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 5 * 4;
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
			+ (this.dDocs.listSize())
	;
	}

}
