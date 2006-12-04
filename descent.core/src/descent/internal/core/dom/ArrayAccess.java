package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IArrayExpression;

/**
 * Array access expression AST node type.
 *
 * <pre>
 * ArrayAccess:
 *    Expression <b>[</b> [ Expression { <b>,</b> Expression } ] <b>]</b>
 * </pre>
 */
public class ArrayAccess extends Expression implements IArrayExpression {
	
	/**
	 * The "array" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor ARRAY_PROPERTY =
		new ChildPropertyDescriptor(ArrayAccess.class, "array", Expression.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "indexes" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor INDEXES_PROPERTY =
		new ChildListPropertyDescriptor(ArrayAccess.class, "indexes", Expression.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(ArrayAccess.class, properyList);
		addProperty(ARRAY_PROPERTY, properyList);
		addProperty(INDEXES_PROPERTY, properyList);
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
	 * The array.
	 */
	private Expression array = null;

	/**
	 * The indexes
	 * (element type: <code>Expression</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList indexes =
		new ASTNode.NodeList(INDEXES_PROPERTY);

	/**
	 * Creates a new unparented array access expression node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ArrayAccess(AST ast) {
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
		if (property == ARRAY_PROPERTY) {
			if (get) {
				return getArray();
			} else {
				setArray((Expression) child);
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
		if (property == INDEXES_PROPERTY) {
			return indexes();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return ARRAY_ACCESS;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		ArrayAccess result = new ArrayAccess(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setArray((Expression) getArray().clone(target));
		result.indexes.addAll(ASTNode.copySubtrees(target, indexes()));
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
			acceptChild(visitor, getArray());
			acceptChildren(visitor, indexes());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the array of this array access expression.
	 * 
	 * @return the array
	 */ 
	public Expression getArray() {
		if (this.array == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.array == null) {
					preLazyInit();
					this.array = new SimpleName(this.ast);
					postLazyInit(this.array, ARRAY_PROPERTY);
				}
			}
		}
		return this.array;
	}

	/**
	 * Sets the array of this array access expression.
	 * 
	 * @param array the array
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setArray(Expression array) {
		if (array == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.array;
		preReplaceChild(oldChild, array, ARRAY_PROPERTY);
		this.array = array;
		postReplaceChild(oldChild, array, ARRAY_PROPERTY);
	}

	/**
	 * Returns the live ordered list of indexes for this
	 * array access expression.
	 * 
	 * @return the live list of array access expression
	 *    (element type: <code>Expression</code>)
	 */ 
	public List<Expression> indexes() {
		return this.indexes;
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
			+ (this.array == null ? 0 : getArray().treeSize())
			+ (this.indexes.listSize());
	}

	// TODO Descent remove
	public ArrayAccess(Expression array, List<Expression> indexes) {
		this.array = array;
		this.indexes.addAll(indexes);
	}

}
