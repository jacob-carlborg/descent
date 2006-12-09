package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IStaticArrayType;

/**
 * Static array type AST node type.
 *
 * <pre>
 * StaticArrayType:
 *    Type <b>[</b> Expression <b>]</b>
 * </pre>
 */
public class StaticArrayType extends ArrayType implements IStaticArrayType {
	
	/**
	 * The "componentType" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor COMPONENT_TYPE_PROPERTY =
		internalComponentTypePropertyFactory(StaticArrayType.class); //$NON-NLS-1$

	/**
	 * The "size" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor SIZE_PROPERTY =
		new ChildPropertyDescriptor(StaticArrayType.class, "size", Expression.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(StaticArrayType.class, properyList);
		addProperty(COMPONENT_TYPE_PROPERTY, properyList);
		addProperty(SIZE_PROPERTY, properyList);
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
	 * The componentType.
	 */
	private Type componentType;

	/**
	 * The size.
	 */
	private Expression size;


	/**
	 * Creates a new unparented static array type node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	StaticArrayType(AST ast) {
		super(ast);
		
		// TODO Descent remove
		this.ty = TY.Tsarray;
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
		if (property == SIZE_PROPERTY) {
			if (get) {
				return getSize();
			} else {
				setSize((Expression) child);
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
		return STATIC_ARRAY_TYPE;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		StaticArrayType result = new StaticArrayType(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setComponentType((Type) getComponentType().clone(target));
	result.setSize((Expression) ASTNode.copySubtree(target, getSize()));
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
			acceptChild(visitor, getSize());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the size of this static array type.
	 * 
	 * @return the size
	 */ 
	public Expression getSize() {
		return this.size;
	}

	/**
	 * Sets the size of this static array type.
	 * 
	 * @param size the size
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setSize(Expression size) {
		ASTNode oldChild = this.size;
		preReplaceChild(oldChild, size, SIZE_PROPERTY);
		this.size = size;
		postReplaceChild(oldChild, size, SIZE_PROPERTY);
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
			+ (this.size == null ? 0 : getSize().treeSize())
	;
	}

}
