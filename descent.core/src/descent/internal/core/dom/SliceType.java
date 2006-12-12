package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.ISliceType;

/**
 * Slice type AST node type.
 *
 * <pre>
 * SliceType:
 *    Type <b>[</b> Expression <b>..</b> Expression <b>]</b>
 * </pre>
 */
public class SliceType extends ArrayType implements ISliceType {

	/**
	 * The "componentType" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor COMPONENT_TYPE_PROPERTY =
		internalComponentTypePropertyFactory(SliceType.class); //$NON-NLS-1$

	/**
	 * The "fromExpression" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor FROM_EXPRESSION_PROPERTY =
		new ChildPropertyDescriptor(SliceType.class, "fromExpression", Expression.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "toExpression" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor TO_EXPRESSION_PROPERTY =
		new ChildPropertyDescriptor(SliceType.class, "toExpression", Expression.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(3);
		createPropertyList(SliceType.class, properyList);
		addProperty(COMPONENT_TYPE_PROPERTY, properyList);
		addProperty(FROM_EXPRESSION_PROPERTY, properyList);
		addProperty(TO_EXPRESSION_PROPERTY, properyList);
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
	 * The fromExpression.
	 */
	private Expression fromExpression;

	/**
	 * The toExpression.
	 */
	private Expression toExpression;


	/**
	 * Creates a new unparented slice type node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	SliceType(AST ast) {
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
		if (property == FROM_EXPRESSION_PROPERTY) {
			if (get) {
				return getFromExpression();
			} else {
				setFromExpression((Expression) child);
				return null;
			}
		}
		if (property == TO_EXPRESSION_PROPERTY) {
			if (get) {
				return getToExpression();
			} else {
				setToExpression((Expression) child);
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
		return SLICE_TYPE;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		SliceType result = new SliceType(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setComponentType((Type) getComponentType().clone(target));
	result.setFromExpression((Expression) ASTNode.copySubtree(target, getFromExpression()));
	result.setToExpression((Expression) ASTNode.copySubtree(target, getToExpression()));
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
			acceptChild(visitor, getFromExpression());
			acceptChild(visitor, getToExpression());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the from expression of this slice type.
	 * 
	 * @return the from expression
	 */ 
	public Expression getFromExpression() {
		return this.fromExpression;
	}

	/**
	 * Sets the from expression of this slice type.
	 * 
	 * @param fromExpression the from expression
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setFromExpression(Expression fromExpression) {
		ASTNode oldChild = this.fromExpression;
		preReplaceChild(oldChild, fromExpression, FROM_EXPRESSION_PROPERTY);
		this.fromExpression = fromExpression;
		postReplaceChild(oldChild, fromExpression, FROM_EXPRESSION_PROPERTY);
	}

	/**
	 * Returns the to expression of this slice type.
	 * 
	 * @return the to expression
	 */ 
	public Expression getToExpression() {
		return this.toExpression;
	}

	/**
	 * Sets the to expression of this slice type.
	 * 
	 * @param toExpression the to expression
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setToExpression(Expression toExpression) {
		ASTNode oldChild = this.toExpression;
		preReplaceChild(oldChild, toExpression, TO_EXPRESSION_PROPERTY);
		this.toExpression = toExpression;
		postReplaceChild(oldChild, toExpression, TO_EXPRESSION_PROPERTY);
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
			+ (this.componentType == null ? 0 : getComponentType().treeSize())
			+ (this.fromExpression == null ? 0 : getFromExpression().treeSize())
			+ (this.toExpression == null ? 0 : getToExpression().treeSize())
	;
	}

}
