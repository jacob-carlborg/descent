package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Each fragment of an associative array literal.
 * 
 * <pre>
 * AssociativeArrayLiteralFragment:
 *    Expression <b>:</b> Expression
 * </pre>
 */
public class AssociativeArrayLiteralFragment extends ASTNode {
	
	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor KEY_PROPERTY =
		new ChildPropertyDescriptor(AssociativeArrayLiteralFragment.class, "key", SimpleName.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "initializer" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor VALUE_PROPERTY =
		new ChildPropertyDescriptor(AssociativeArrayLiteralFragment.class, "value", Initializer.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(AssociativeArrayLiteralFragment.class, properyList);
		addProperty(KEY_PROPERTY, properyList);
		addProperty(VALUE_PROPERTY, properyList);
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
	 * The name.
	 */
	private Expression key;

	/**
	 * The value.
	 */
	private Expression value;


	/**
	 * Creates a new unparented associative array literal fragment node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	AssociativeArrayLiteralFragment(AST ast) {
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
		if (property == KEY_PROPERTY) {
			if (get) {
				return getKey();
			} else {
				setKey((Expression) child);
				return null;
			}
		}
		if (property == VALUE_PROPERTY) {
			if (get) {
				return getValue();
			} else {
				setValue((Expression) child);
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
		return ASSOCIATIVE_ARRAY_LITERAL_FRAGMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		AssociativeArrayLiteralFragment result = new AssociativeArrayLiteralFragment(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setKey((Expression) ASTNode.copySubtree(target, getKey()));
		result.setValue((Expression) ASTNode.copySubtree(target, getValue()));
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
			acceptChild(visitor, getKey());
			acceptChild(visitor, getValue());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the key of this associative array literal fragment.
	 * 
	 * @return the key
	 */ 
	public Expression getKey() {
		return this.key;
	}

	/**
	 * Sets the key of this associative array literal fragment.
	 * 
	 * @param key the key
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setKey(Expression key) {
		ASTNode oldChild = this.key;
		preReplaceChild(oldChild, key, KEY_PROPERTY);
		this.key = key;
		postReplaceChild(oldChild, key, KEY_PROPERTY);
	}

	/**
	 * Returns the value of this associative array literal fragment.
	 * 
	 * @return the value
	 */ 
	public Expression getValue() {
		return this.value;
	}

	/**
	 * Sets the value of this associative array literal fragment.
	 * 
	 * @param value the value
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setValue(Expression value) {
		if (value == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.value;
		preReplaceChild(oldChild, value, VALUE_PROPERTY);
		this.value = value;
		postReplaceChild(oldChild, value, VALUE_PROPERTY);
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
			+ (this.key == null ? 0 : getKey().treeSize())
			+ (this.value == null ? 0 : getValue().treeSize())
	;
	}

}
