package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IEnumMember;

/**
 * Enum member AST node type.
 *
 * <pre>
 * EnumMember:
 *    SimpleName [ <b>=</b> Expression ]
 * </pre>
 */
public class EnumMember extends ASTNode implements IEnumMember {
	
	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY = 
		new ChildPropertyDescriptor(EnumMember.class, "name", SimpleName.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * The "value" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor VALUE_PROPERTY = 
		new ChildPropertyDescriptor(EnumMember.class, "value", Expression.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;
	
	static {
		List propertyList = new ArrayList(3);
		createPropertyList(EnumMember.class, propertyList);
		addProperty(NAME_PROPERTY, propertyList);
		addProperty(VALUE_PROPERTY, propertyList);
		PROPERTY_DESCRIPTORS = reapPropertyList(propertyList);
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
	
	private SimpleName name = null;
	private Expression value = null;
	
	/**
	 * Creates a new unparented enum member node owned by the given 
	 * AST. By default, TODO.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	EnumMember(AST ast) {
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
		if (property == NAME_PROPERTY) {
			if (get) {
				return getName();
			} else {
				setName((SimpleName) child);
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
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return ENUM_MEMBER;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		EnumMember result = new EnumMember(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setName((SimpleName) getName().clone(target));
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
			acceptChild(visitor, getName());
			acceptChild(visitor, getValue());
		}
		visitor.endVisit(this);
	}
	
	/**
	 * Returns the name of this argument.
	 * 
	 * @return the simple name node
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
	 * Sets the name of this argument.
	 * 
	 * @param argumentName the name node
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setName(SimpleName argumentName) {
		if (argumentName == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.name;
		preReplaceChild(oldChild, argumentName, NAME_PROPERTY);
		this.name = argumentName;
		postReplaceChild(oldChild, argumentName, NAME_PROPERTY);
	}
	
	/**
	 * Returns the default value of this argument.
	 * 
	 * @return the expression node
	 */ 
	public Expression getValue() {
		if (this.value == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.value == null) {
					preLazyInit();
					this.value = new SimpleName(this.ast);
					postLazyInit(this.value, VALUE_PROPERTY);
				}
			}
		}
		return this.value;
	}
	
	/**
	 * Sets the value of this argument.
	 * 
	 * @param value the expression node
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
		return super.memSize() + 2 * 4;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.name == null ? 0 : getName().treeSize())
			+ (this.value == null ? 0 : getValue().treeSize());
	}

	// TODO descent remove
	public EnumMember(SimpleName name, Expression value) {
		super(AST.newAST(AST.JLS3));
		this.name = name;
		this.value = value;
		this.startPosition = name.startPosition;
		if (value == null) {
			this.length = this.name.length;
		} else {
			this.length = value.startPosition + value.length - this.startPosition;
		}
	}
	
}
