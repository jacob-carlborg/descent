package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Value template parameter AST node type.
 *
 * <pre>
 * ValueTemplateParameter:
 *    Type SimpleName [ <b>:</b> Expression ] [ <b>=</b> Expression ]
 * </pre>
 */
public class ValueTemplateParameter extends TemplateParameter {

	/**
	 * The "type" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor TYPE_PROPERTY =
		new ChildPropertyDescriptor(ValueTemplateParameter.class, "type", Type.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY =
		new ChildPropertyDescriptor(ValueTemplateParameter.class, "name", SimpleName.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "specificValue" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor SPECIFIC_VALUE_PROPERTY =
		new ChildPropertyDescriptor(ValueTemplateParameter.class, "specificValue", Expression.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "defaultValue" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor DEFAULT_VALUE_PROPERTY =
		new ChildPropertyDescriptor(ValueTemplateParameter.class, "defaultValue", Expression.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(4);
		createPropertyList(ValueTemplateParameter.class, properyList);
		addProperty(TYPE_PROPERTY, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(SPECIFIC_VALUE_PROPERTY, properyList);
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
	 * The type.
	 */
	private Type type;

	/**
	 * The name.
	 */
	private SimpleName name;

	/**
	 * The specificValue.
	 */
	private Expression specificValue;

	/**
	 * The defaultValue.
	 */
	private Expression defaultValue;


	/**
	 * Creates a new unparented value template parameter node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ValueTemplateParameter(AST ast) {
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
		if (property == SPECIFIC_VALUE_PROPERTY) {
			if (get) {
				return getSpecificValue();
			} else {
				setSpecificValue((Expression) child);
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
		return VALUE_TEMPLATE_PARAMETER;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		ValueTemplateParameter result = new ValueTemplateParameter(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setType((Type) getType().clone(target));
		result.setName((SimpleName) getName().clone(target));
	result.setSpecificValue((Expression) ASTNode.copySubtree(target, getSpecificValue()));
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
			acceptChild(visitor, getSpecificValue());
			acceptChild(visitor, getDefaultValue());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the type of this value template parameter.
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
	 * Sets the type of this value template parameter.
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

	/**
	 * Returns the name of this value template parameter.
	 * 
	 * @return the name
	 */ 
	public SimpleName getName() {
		if (this.name == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.name == null) {
					preLazyInit();
					this.name = new SimpleName(this.ast);
					postLazyInit(this.name, NAME_PROPERTY);
				}
			}
		}
		return this.name;
	}

	/**
	 * Sets the name of this value template parameter.
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
		if (name == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.name;
		preReplaceChild(oldChild, name, NAME_PROPERTY);
		this.name = name;
		postReplaceChild(oldChild, name, NAME_PROPERTY);
	}

	/**
	 * Returns the specific value of this value template parameter.
	 * 
	 * @return the specific value
	 */ 
	public Expression getSpecificValue() {
		return this.specificValue;
	}

	/**
	 * Sets the specific value of this value template parameter.
	 * 
	 * @param specificValue the specific value
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setSpecificValue(Expression specificValue) {
		ASTNode oldChild = this.specificValue;
		preReplaceChild(oldChild, specificValue, SPECIFIC_VALUE_PROPERTY);
		this.specificValue = specificValue;
		postReplaceChild(oldChild, specificValue, SPECIFIC_VALUE_PROPERTY);
	}

	/**
	 * Returns the default value of this value template parameter.
	 * 
	 * @return the default value
	 */ 
	public Expression getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * Sets the default value of this value template parameter.
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
			+ (this.specificValue == null ? 0 : getSpecificValue().treeSize())
			+ (this.defaultValue == null ? 0 : getDefaultValue().treeSize())
	;
	}

}
