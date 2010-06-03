package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Modified type AST node.
 * 
 * <pre>
 * ModifiedType:
 *    Modifier ( <b>(</b> Type <b>)</b> | Type ) 
 * </pre>
 */
public class ModifiedType extends Type {
	
	/**
	 * The "modifier" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor MODIFIER_PROPERTY =
		new ChildPropertyDescriptor(ModifiedType.class, "modifier", Modifier.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "componentType" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor COMPONENT_TYPE_PROPERTY =
		new ChildPropertyDescriptor(ModifiedType.class, "componentType", Type.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(ModifiedType.class, properyList);
		addProperty(MODIFIER_PROPERTY, properyList);
		addProperty(COMPONENT_TYPE_PROPERTY, properyList);
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
	 * The componentType.
	 */
	private Type componentType;


	/**
	 * Creates a new unparented modified type node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ModifiedType(AST ast) {
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
		if (property == COMPONENT_TYPE_PROPERTY) {
			if (get) {
				return getComponentType();
			} else {
				setComponentType((Type) child);
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
		return MODIFIED_TYPE;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		ModifiedType result = new ModifiedType(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setModifier((Modifier) getModifier().clone(target));
		result.setComponentType((Type) getComponentType().clone(target));
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
			acceptChild(visitor, getComponentType());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the modifier of this modified type.
	 * 
	 * @return the modifier
	 */ 
	public Modifier getModifier() {
		if (this.modifier == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.modifier == null) {
					preLazyInit();
					this.modifier = new Modifier(this.ast);
					postLazyInit(this.modifier, MODIFIER_PROPERTY);
				}
			}
		}
		return this.modifier;
	}

	/**
	 * Sets the modifier of this modified type.
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
		if (modifier == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.modifier;
		preReplaceChild(oldChild, modifier, MODIFIER_PROPERTY);
		this.modifier = modifier;
		postReplaceChild(oldChild, modifier, MODIFIER_PROPERTY);
	}

	/**
	 * Returns the component type of this modified type.
	 * 
	 * @return the component type
	 */ 
	public Type getComponentType() {
		if (this.componentType == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.componentType == null) {
					preLazyInit();
					this.componentType = new PrimitiveType(this.ast);
					postLazyInit(this.componentType, COMPONENT_TYPE_PROPERTY);
				}
			}
		}
		return this.componentType;
	}

	/**
	 * Sets the component type of this modified type.
	 * 
	 * @param componentType the component type
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setComponentType(Type componentType) {
		if (componentType == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.componentType;
		preReplaceChild(oldChild, componentType, COMPONENT_TYPE_PROPERTY);
		this.componentType = componentType;
		postReplaceChild(oldChild, componentType, COMPONENT_TYPE_PROPERTY);
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
			+ (this.componentType == null ? 0 : getComponentType().treeSize())
	;
	}

}
