package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IArgument;

/**
 * Argument AST node type. An argument is the one passed to a function
 * or method.
 *
 * <pre>
 * Argument:
 *    [ | <b>in</b> | <b>out</b> | <b>inout</b> | <b>lazy</b> ] Type SimpleName [ <b>=</b> Expression ]
 * </pre>
 */
public class Argument extends ASTNode implements IArgument {
	
	/**
	 * The "passage mode" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor PASSAGE_MODE_PROPERTY = 
		new SimplePropertyDescriptor(Argument.class, "passageMode", PassageMode.class, MANDATORY); //$NON-NLS-1$
	
	/**
	 * The "type" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor TYPE_PROPERTY = 
		new ChildPropertyDescriptor(Argument.class, "type", Type.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY = 
		new ChildPropertyDescriptor(Argument.class, "name", SimpleName.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * The "defaultValue" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor DEFAULT_VALUE_PROPERTY = 
		new ChildPropertyDescriptor(Argument.class, "defaultValue", Expression.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;
	
	static {
		List propertyList = new ArrayList(3);
		createPropertyList(Argument.class, propertyList);
		addProperty(PASSAGE_MODE_PROPERTY, propertyList);
		addProperty(TYPE_PROPERTY, propertyList);
		addProperty(NAME_PROPERTY, propertyList);
		addProperty(DEFAULT_VALUE_PROPERTY, propertyList);
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
	
	// TODO comment better
	public PassageMode passageMode = PassageMode.IN;
	public Type type;
	public SimpleName argumentName;	
	public Expression defaultValue;
	
	/**
	 * Creates a new unparented argument node owned by the given 
	 * AST. By default, TODO.
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
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return ARGUMENT;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		Argument result = new Argument(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setPassageMode(getPassageMode());
		result.setType((Type) getType().clone(target));
		result.setName((SimpleName) getName().clone(target));
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
	 * @return the passage mode node
	 */
	public PassageMode getPassageMode() {
		return passageMode;
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
	 * @return the expression node
	 */ 
	public Type getType() {
		if (this.type == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.type == null) {
					preLazyInit();
					// TODO correct
					this.type = Type.tint32;
					postLazyInit(this.type, TYPE_PROPERTY);
				}
			}
		}
		return this.type;
	}
	
	/**
	 * Sets the type of this argument.
	 * 
	 * @param type the type node
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
	 * Returns the name of this argument.
	 * 
	 * @return the simple name node
	 */ 
	public SimpleName getName() {
		if (this.argumentName == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.argumentName == null) {
					preLazyInit();
					this.argumentName = new SimpleName(this.ast);
					postLazyInit(this.argumentName, NAME_PROPERTY);
				}
			}
		}
		return this.argumentName;
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
		ASTNode oldChild = this.argumentName;
		preReplaceChild(oldChild, argumentName, NAME_PROPERTY);
		this.argumentName = argumentName;
		postReplaceChild(oldChild, argumentName, NAME_PROPERTY);
	}
	
	/**
	 * Returns the default value of this argument.
	 * 
	 * @return the expression node
	 */ 
	public Expression getDefaultValue() {
		if (this.defaultValue == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.defaultValue == null) {
					preLazyInit();
					this.defaultValue = new SimpleName(this.ast);
					postLazyInit(this.defaultValue, NAME_PROPERTY);
				}
			}
		}
		return this.defaultValue;
	}
	
	/**
	 * Sets the default value of this argument.
	 * 
	 * @param defaultValue the expression node
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setDefaultValue(Expression defaultValue) {
		if (defaultValue == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.defaultValue;
		preReplaceChild(oldChild, defaultValue, DEFAULT_VALUE_PROPERTY);
		this.defaultValue = defaultValue;
		postReplaceChild(oldChild, defaultValue, DEFAULT_VALUE_PROPERTY);
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return super.memSize() + 3 * 4;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.type == null ? 0 : getType().treeSize())
			+ (this.argumentName == null ? 0 : getName().treeSize())
			+ (this.defaultValue == null ? 0 : getDefaultValue().treeSize());
	}

	// TODO Descent remove
	public Argument(PassageMode passageMode, Type type, SimpleName name, Expression defaultValue) {
		super(AST.newAST(AST.JLS3));
		this.argumentName = name;
		this.type = type;
		this.passageMode = passageMode;
		this.defaultValue = defaultValue;
	}
	
	public int getKind() {
		switch(passageMode) {
		case IN: return IArgument.IN;
		case OUT: return IArgument.OUT;
		case INOUT: return IArgument.INOUT;
		default /* case Lazy */: return IArgument.LAZY;
		} 
	}

}
