package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Associative array type AST node type.
 *
 * <pre>
 * AssociativeArrayType:
 *    Type <b>[</b> Type <b>]</b>
 * </pre>
 */
public class AssociativeArrayType extends ArrayType {

	/**
	 * The "componentType" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor COMPONENT_TYPE_PROPERTY = 
		internalComponentTypePropertyFactory(AssociativeArrayType.class); //$NON-NLS-1$

	/**
	 * The "keyType" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor KEY_TYPE_PROPERTY =
		new ChildPropertyDescriptor(AssociativeArrayType.class, "keyType", Type.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(AssociativeArrayType.class, properyList);
		addProperty(COMPONENT_TYPE_PROPERTY, properyList);
		addProperty(KEY_TYPE_PROPERTY, properyList);
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
	 * The keyType.
	 */
	private Type keyType;


	/**
	 * Creates a new unparented associative array type node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	AssociativeArrayType(AST ast) {
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
		if (property == COMPONENT_TYPE_PROPERTY) {
			if (get) {
				return getComponentType();
			} else {
				setComponentType((Type) child);
				return null;
			}
		}
		if (property == KEY_TYPE_PROPERTY) {
			if (get) {
				return getKeyType();
			} else {
				setKeyType((Type) child);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetChildProperty(property, get, child);
	}
	
	@Override
	final ChildPropertyDescriptor internalComponentTypeProperty() {
		return COMPONENT_TYPE_PROPERTY;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return ASSOCIATIVE_ARRAY_TYPE;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		AssociativeArrayType result = new AssociativeArrayType(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setComponentType((Type) getComponentType().clone(target));
		result.setKeyType((Type) getKeyType().clone(target));
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
			acceptChild(visitor, getComponentType());
			acceptChild(visitor, getKeyType());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the key type of this associative array type.
	 * 
	 * @return the key type
	 */ 
	public Type getKeyType() {
		if (this.keyType == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.keyType == null) {
					preLazyInit();
					this.keyType = new PrimitiveType(this.ast);
					postLazyInit(this.keyType, KEY_TYPE_PROPERTY);
				}
			}
		}
		return this.keyType;
	}

	/**
	 * Sets the key type of this associative array type.
	 * 
	 * @param keyType the key type
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setKeyType(Type keyType) {
		if (keyType == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.keyType;
		preReplaceChild(oldChild, keyType, KEY_TYPE_PROPERTY);
		this.keyType = keyType;
		postReplaceChild(oldChild, keyType, KEY_TYPE_PROPERTY);
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
			+ (this.componentType == null ? 0 : getComponentType().treeSize())
			+ (this.keyType == null ? 0 : getKeyType().treeSize())
	;
	}

}
