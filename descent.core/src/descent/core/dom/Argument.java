package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Argument AST node type. An argument is the one passed to a function
 * or method.
 *
 * <pre>
 * Argument:
 *    [ | <b>in</b> | <b>out</b> | <b>inout</b> | <b>lazy</b> ] [ Type ] [ SimpleName ] [ <b>=</b> Expression ]
 * </pre>
 */
public class Argument extends ASTNode {
	
	/**
	 * The passage mode of the argument.
	 * TODO: comment better
	 */
	public static enum PassageMode {
		/** "in" passage mode */
		IN,
		/** "out" passage mode */
		OUT,
		/** "inout" passage mode */
		INOUT,
		/** "lazy" passage mode */
		LAZY
	}

	/**
	 * The "passageMode" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor PASSAGE_MODE_PROPERTY =
		new SimplePropertyDescriptor(Argument.class, "passageMode", PassageMode.class, MANDATORY); //$NON-NLS-1$

	/**
	 * The "type" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor TYPE_PROPERTY =
		new ChildPropertyDescriptor(Argument.class, "type", Type.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY =
		new ChildPropertyDescriptor(Argument.class, "name", SimpleName.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "defaultValue" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor DEFAULT_VALUE_PROPERTY =
		new ChildPropertyDescriptor(Argument.class, "defaultValue", Expression.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(4);
		createPropertyList(Argument.class, properyList);
		addProperty(PASSAGE_MODE_PROPERTY, properyList);
		addProperty(TYPE_PROPERTY, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(DEFAULT_VALUE_PROPERTY, properyList);
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
	 * The passageMode.
	 */
	private PassageMode passageMode;

	/**
	 * The type.
	 */
	private Type type;

	/**
	 * The name.
	 */
	private SimpleName name;

	/**
	 * The defaultValue.
	 */
	private Expression defaultValue;


	/**
	 * Creates a new unparented argument node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	Argument(AST ast) {
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
	final Object internalGetSetObjectProperty(SimplePropertyDescriptor property, boolean get, Object value) {
		if (property == PASSAGE_MODE_PROPERTY) {
			if (get) {
				return getPassageMode();
			} else {
				setPassageMode((PassageMode) value);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetObjectProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == TYPE_PROPERTY) {
			if (get) {
				return getType();
			} else {
				setType((Type) child);
				return null;
			}
		}
		if (property == NAME_PROPERTY) {
			if (get) {
				return getName();
			} else {
				setName((SimpleName) child);
				return null;
			}
		}
		if (property == DEFAULT_VALUE_PROPERTY) {
			if (get) {
				return getDefaultValue();
			} else {
				setDefaultValue((Expression) child);
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
		return ARGUMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		Argument result = new Argument(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setPassageMode(getPassageMode());
	result.setType((Type) ASTNode.copySubtree(target, getType()));
	result.setName((SimpleName) ASTNode.copySubtree(target, getName()));
	result.setDefaultValue((Expression) ASTNode.copySubtree(target, getDefaultValue()));
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
			acceptChild(visitor, getType());
			acceptChild(visitor, getName());
			acceptChild(visitor, getDefaultValue());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the passage mode of this argument.
	 * 
	 * @return the passage mode
	 */ 
	public PassageMode getPassageMode() {
		return this.passageMode;
	}

	/**
	 * Sets the passage mode of this argument.
	 * 
	 * @param passageMode the passage mode
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setPassageMode(PassageMode passageMode) {
		if (passageMode == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(PASSAGE_MODE_PROPERTY);
		this.passageMode = passageMode;
		postValueChange(PASSAGE_MODE_PROPERTY);
	}

	/**
	 * Returns the type of this argument.
	 * 
	 * @return the type
	 */ 
	public Type getType() {
		return this.type;
	}

	/**
	 * Sets the type of this argument.
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
		ASTNode oldChild = this.type;
		preReplaceChild(oldChild, type, TYPE_PROPERTY);
		this.type = type;
		postReplaceChild(oldChild, type, TYPE_PROPERTY);
	}

	/**
	 * Returns the name of this argument.
	 * 
	 * @return the name
	 */ 
	public SimpleName getName() {
		return this.name;
	}

	/**
	 * Sets the name of this argument.
	 * 
	 * @param name the name
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setName(SimpleName name) {
		ASTNode oldChild = this.name;
		preReplaceChild(oldChild, name, NAME_PROPERTY);
		this.name = name;
		postReplaceChild(oldChild, name, NAME_PROPERTY);
	}

	/**
	 * Returns the default value of this argument.
	 * 
	 * @return the default value
	 */ 
	public Expression getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * Sets the default value of this argument.
	 * 
	 * @param defaultValue the default value
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setDefaultValue(Expression defaultValue) {
		ASTNode oldChild = this.defaultValue;
		preReplaceChild(oldChild, defaultValue, DEFAULT_VALUE_PROPERTY);
		this.defaultValue = defaultValue;
		postReplaceChild(oldChild, defaultValue, DEFAULT_VALUE_PROPERTY);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 4 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.type == null ? 0 : getType().treeSize())
			+ (this.name == null ? 0 : getName().treeSize())
			+ (this.defaultValue == null ? 0 : getDefaultValue().treeSize())
	;
	}

}
