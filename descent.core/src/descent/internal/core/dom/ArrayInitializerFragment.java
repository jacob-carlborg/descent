package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;

/**
 * Each fragment of an array initializer.
 * 
 * <pre>
 * ArrayInitializerFramgnet:
 *    [ Expression <b>:</b> ] Initializer
 * </pre>
 */
public class ArrayInitializerFragment extends ASTNode {
	
	/**
	 * The "expression" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor EXPRESSION_PROPERTY =
		new ChildPropertyDescriptor(ArrayInitializerFragment.class, "expression", Expression.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "initializer" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor INITIALIZER_PROPERTY =
		new ChildPropertyDescriptor(ArrayInitializerFragment.class, "initializer", Initializer.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(ArrayInitializerFragment.class, properyList);
		addProperty(EXPRESSION_PROPERTY, properyList);
		addProperty(INITIALIZER_PROPERTY, properyList);
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
	 * The expression.
	 */
	private Expression expression;

	/**
	 * The initializer.
	 */
	private Initializer initializer;


	/**
	 * Creates a new unparented array initializer fragment node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ArrayInitializerFragment(AST ast) {
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
		if (property == EXPRESSION_PROPERTY) {
			if (get) {
				return getExpression();
			} else {
				setExpression((Expression) child);
				return null;
			}
		}
		if (property == INITIALIZER_PROPERTY) {
			if (get) {
				return getInitializer();
			} else {
				setInitializer((Initializer) child);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetChildProperty(property, get, child);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return ARRAY_INITIALIZER_FRAGMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		ArrayInitializerFragment result = new ArrayInitializerFragment(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
	result.setExpression((Expression) ASTNode.copySubtree(target, getExpression()));
		result.setInitializer((Initializer) getInitializer().clone(target));
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
			acceptChild(visitor, getExpression());
			acceptChild(visitor, getInitializer());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the expression of this array initializer fragment.
	 * 
	 * @return the expression
	 */ 
	public Expression getExpression() {
		return this.expression;
	}

	/**
	 * Sets the expression of this array initializer fragment.
	 * 
	 * @param expression the expression
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setExpression(Expression expression) {
		ASTNode oldChild = this.expression;
		preReplaceChild(oldChild, expression, EXPRESSION_PROPERTY);
		this.expression = expression;
		postReplaceChild(oldChild, expression, EXPRESSION_PROPERTY);
	}

	/**
	 * Returns the initializer of this array initializer fragment.
	 * 
	 * @return the initializer
	 */ 
	public Initializer getInitializer() {
		if (this.initializer == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.initializer == null) {
					preLazyInit();
					this.initializer = new VoidInitializer(this.ast);
					postLazyInit(this.initializer, INITIALIZER_PROPERTY);
				}
			}
		}
		return this.initializer;
	}

	/**
	 * Sets the initializer of this array initializer fragment.
	 * 
	 * @param initializer the initializer
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setInitializer(Initializer initializer) {
		if (initializer == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.initializer;
		preReplaceChild(oldChild, initializer, INITIALIZER_PROPERTY);
		this.initializer = initializer;
		postReplaceChild(oldChild, initializer, INITIALIZER_PROPERTY);
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
			+ (this.expression == null ? 0 : getExpression().treeSize())
			+ (this.initializer == null ? 0 : getInitializer().treeSize())
	;
	}

}
