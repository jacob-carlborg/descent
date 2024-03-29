package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Base class AST node.
 * 
 * <pre>
 * BaseClass:
 *    [ Modifier ] Type
 * </pre>
 */
public class BaseClass extends ASTNode {
	
	/**
	 * The "modifier" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor MODIFIER_PROPERTY =
		new ChildPropertyDescriptor(BaseClass.class, "modifier", Modifier.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "type" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor TYPE_PROPERTY =
		new ChildPropertyDescriptor(BaseClass.class, "type", Type.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(BaseClass.class, properyList);
		addProperty(MODIFIER_PROPERTY, properyList);
		addProperty(TYPE_PROPERTY, properyList);
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
	 * The modifier.
	 */
	private Modifier modifier;

	/**
	 * The type.
	 */
	private Type type;


	/**
	 * Creates a new unparented base class node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	BaseClass(AST ast) {
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
		if (property == MODIFIER_PROPERTY) {
			if (get) {
				return getModifier();
			} else {
				setModifier((Modifier) child);
				return null;
			}
		}
		if (property == TYPE_PROPERTY) {
			if (get) {
				return getType();
			} else {
				setType((Type) child);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetChildProperty(property, get, child);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return BASE_CLASS;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		BaseClass result = new BaseClass(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
	result.setModifier((Modifier) ASTNode.copySubtree(target, getModifier()));
		result.setType((Type) getType().clone(target));
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
			acceptChild(visitor, getModifier());
			acceptChild(visitor, getType());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the modifier of this base class.
	 * 
	 * @return the modifier
	 */ 
	public Modifier getModifier() {
		return this.modifier;
	}

	/**
	 * Sets the modifier of this base class.
	 * 
	 * @param modifier the modifier
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setModifier(Modifier modifier) {
		ASTNode oldChild = this.modifier;
		preReplaceChild(oldChild, modifier, MODIFIER_PROPERTY);
		this.modifier = modifier;
		postReplaceChild(oldChild, modifier, MODIFIER_PROPERTY);
	}

	/**
	 * Returns the type of this base class.
	 * 
	 * @return the type
	 */ 
	public Type getType() {
		if (this.type == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.type == null) {
					preLazyInit();
					this.type = new PrimitiveType(this.ast);
					postLazyInit(this.type, TYPE_PROPERTY);
				}
			}
		}
		return this.type;
	}

	/**
	 * Sets the type of this base class.
	 * 
	 * @param type the type
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setType(Type type) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.type;
		preReplaceChild(oldChild, type, TYPE_PROPERTY);
		this.type = type;
		postReplaceChild(oldChild, type, TYPE_PROPERTY);
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
			+ (this.modifier == null ? 0 : getModifier().treeSize())
			+ (this.type == null ? 0 : getType().treeSize())
	;
	}

}
