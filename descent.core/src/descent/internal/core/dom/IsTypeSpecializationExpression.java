package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IIsTypeSpecializationExpression;

/**
 * An is expression ASTNode where the comparison is made against a type specialization keyword.
 * 
 * <pre>
 * IsTypeSpecializationExpression:
 *    <b>is</b> <b>(</b> Type [ [ <b>:</b> | <b>==</b> ] TypeSpecialization ] <b>)</b>
 *    
 * TypeSpecialization:
 *    <b>typedef</b> | <b>struct</b> | <b>union</b> | <b>class</b> | <b>enum</b> | <b>interface</b> | <b>function</b> | <b>delegate</b> | <b>return</b> | <b>super</b> 
 * </pre>
 */
public class IsTypeSpecializationExpression extends Expression implements IIsTypeSpecializationExpression {
	
	/**
	 * A type specialization keyword.
	 */
	public static enum TypeSpecialization {
		TYPEDEF,
		STRUCT,
		UNION,
		CLASS,
		ENUM,
		INTERFACE,
		FUNCTION,
		DELEGATE,
		RETURN,
		SUPER,
	}

	/**
	 * The "sameComparison" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor SAME_COMPARISON_PROPERTY =
		new SimplePropertyDescriptor(IsTypeSpecializationExpression.class, "sameComparison", boolean.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY =
		new ChildPropertyDescriptor(IsTypeSpecializationExpression.class, "name", SimpleName.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "type" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor TYPE_PROPERTY =
		new ChildPropertyDescriptor(IsTypeSpecializationExpression.class, "type", Type.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "specialization" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor SPECIALIZATION_PROPERTY =
		new SimplePropertyDescriptor(IsTypeSpecializationExpression.class, "specialization", TypeSpecialization.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(4);
		createPropertyList(IsTypeSpecializationExpression.class, properyList);
		addProperty(SAME_COMPARISON_PROPERTY, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(TYPE_PROPERTY, properyList);
		addProperty(SPECIALIZATION_PROPERTY, properyList);
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
	 * The sameComparison.
	 */
	private boolean sameComparison;

	/**
	 * The name.
	 */
	private SimpleName name;

	/**
	 * The type.
	 */
	private Type type;

	/**
	 * The specialization.
	 */
	private TypeSpecialization specialization;


	/**
	 * Creates a new unparented is type specialization expression node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	IsTypeSpecializationExpression(AST ast) {
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
		if (property == SPECIALIZATION_PROPERTY) {
			if (get) {
				return getSpecialization();
			} else {
				setSpecialization((TypeSpecialization) value);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetObjectProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final boolean internalGetSetBooleanProperty(SimplePropertyDescriptor property, boolean get, boolean value) {
		if (property == SAME_COMPARISON_PROPERTY) {
			if (get) {
				return isSameComparison();
			} else {
				setSameComparison(value);
				return false;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetBooleanProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == NAME_PROPERTY) {
			if (get) {
				return getName();
			} else {
				setName((SimpleName) child);
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
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return IS_TYPE_SPECIALIZATION_EXPRESSION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		IsTypeSpecializationExpression result = new IsTypeSpecializationExpression(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setSameComparison(isSameComparison());
	result.setName((SimpleName) ASTNode.copySubtree(target, getName()));
		result.setType((Type) getType().clone(target));
		result.setSpecialization(getSpecialization());
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
			acceptChild(visitor, getName());
			acceptChild(visitor, getType());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the same comparison of this is type expression. The same
	 * comparison is true if the type equality (<b>==</b>) is compared, instead
	 * of assignability (<b>:</b>).
	 * 
	 * @return the same comparison
	 */ 
	public boolean isSameComparison() {
		return this.sameComparison;
	}

	/**
	 * Sets the same comparison of this is type expression. The same
	 * comparison is true if the type equality (<b>==</b>) is compared, instead
	 * of assignability (<b>:</b>).
	 * 
	 * @param sameComparison the same comparison
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setSameComparison(boolean sameComparison) {
		preValueChange(SAME_COMPARISON_PROPERTY);
		this.sameComparison = sameComparison;
		postValueChange(SAME_COMPARISON_PROPERTY);
	}

	/**
	 * Returns the name of this is type specialization expression.
	 * 
	 * @return the name
	 */ 
	public SimpleName getName() {
		return this.name;
	}

	/**
	 * Sets the name of this is type specialization expression.
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
	 * Returns the type of this is type specialization expression.
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
	 * Sets the type of this is type specialization expression.
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
	 * Returns the specialization of this is type specialization expression.
	 * 
	 * @return the specialization
	 */ 
	public TypeSpecialization getSpecialization() {
		return this.specialization;
	}

	/**
	 * Sets the specialization of this is type specialization expression.
	 * 
	 * @param specialization the specialization
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setSpecialization(TypeSpecialization specialization) {
		if (specialization == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(SPECIALIZATION_PROPERTY);
		this.specialization = specialization;
		postValueChange(SPECIALIZATION_PROPERTY);
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
			+ (this.name == null ? 0 : getName().treeSize())
			+ (this.type == null ? 0 : getType().treeSize())
	;
	}

}
