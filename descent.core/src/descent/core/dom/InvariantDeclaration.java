package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Invariant declaration AST node type.
 *
 * <pre>
 * InvariantDeclaration:
 *    { Modifier } <b>invariant</b> Statement
 * </pre>
 */
public class InvariantDeclaration extends Declaration {

	/**
	 * The "modifiers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor MODIFIERS_PROPERTY =
	internalModifiersPropertyFactory(InvariantDeclaration.class); //$NON-NLS-1$

	/**
	 * The "body" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor BODY_PROPERTY =
		new ChildPropertyDescriptor(InvariantDeclaration.class, "body", Statement.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(InvariantDeclaration.class, properyList);
		addProperty(MODIFIERS_PROPERTY, properyList);
		addProperty(BODY_PROPERTY, properyList);
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
	 * The body.
	 */
	private Statement body;


	/**
	 * Creates a new unparented invariant declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	InvariantDeclaration(AST ast) {
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
		if (property == BODY_PROPERTY) {
			if (get) {
				return getBody();
			} else {
				setBody((Statement) child);
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
		return INVARIANT_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		InvariantDeclaration result = new InvariantDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.modifiers.addAll(ASTNode.copySubtrees(target, modifiers()));
		result.setBody((Statement) getBody().clone(target));
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
			acceptChild(visitor, getBody());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the body of this invariant declaration.
	 * 
	 * @return the body
	 */ 
	public Statement getBody() {
		if (this.body == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.body == null) {
					preLazyInit();
					this.body = new Block(this.ast);
					postLazyInit(this.body, BODY_PROPERTY);
				}
			}
		}
		return this.body;
	}

	/**
	 * Sets the body of this invariant declaration.
	 * 
	 * @param body the body
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setBody(Statement body) {
		if (body == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.body;
		preReplaceChild(oldChild, body, BODY_PROPERTY);
		this.body = body;
		postReplaceChild(oldChild, body, BODY_PROPERTY);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 2 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.modifiers.listSize())
			+ (this.body == null ? 0 : getBody().treeSize())
	;
	}

}