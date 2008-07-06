package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * A delegate or function pointer type AST node.
 * 
 * <pre>
 * DelegateType:
 *    Type [ <b>delegate</b> | <b>function</b> ] <b>(</b> [ Argument { <b>,</b> Argument } ] <b>)</b>
 * </pre>
 */
public class DelegateType extends Type {
	
	/**
	 * The "variadic" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor VARIADIC_PROPERTY =
		new SimplePropertyDescriptor(DelegateType.class, "variadic", boolean.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "functionPointer" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor FUNCTION_POINTER_PROPERTY =
		new SimplePropertyDescriptor(DelegateType.class, "functionPointer", boolean.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "returnType" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor RETURN_TYPE_PROPERTY =
		new ChildPropertyDescriptor(DelegateType.class, "returnType", Type.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "arguments" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor ARGUMENTS_PROPERTY =
		new ChildListPropertyDescriptor(DelegateType.class, "arguments", Argument.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(4);
		createPropertyList(DelegateType.class, properyList);
		addProperty(VARIADIC_PROPERTY, properyList);
		addProperty(FUNCTION_POINTER_PROPERTY, properyList);
		addProperty(RETURN_TYPE_PROPERTY, properyList);
		addProperty(ARGUMENTS_PROPERTY, properyList);
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
	 * The variadic.
	 */
	private boolean variadic;

	/**
	 * The functionPointer.
	 */
	private boolean functionPointer;

	/**
	 * The returnType.
	 */
	private Type returnType;

	/**
	 * The arguments
	 * (element type: <code>Argument</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList arguments =
		new ASTNode.NodeList(ARGUMENTS_PROPERTY);

	/**
	 * Creates a new unparented delegate type node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	DelegateType(AST ast) {
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
	final boolean internalGetSetBooleanProperty(SimplePropertyDescriptor property, boolean get, boolean value) {
		if (property == VARIADIC_PROPERTY) {
			if (get) {
				return isVariadic();
			} else {
				setVariadic(value);
				return false;
			}
		}
		if (property == FUNCTION_POINTER_PROPERTY) {
			if (get) {
				return isFunctionPointer();
			} else {
				setFunctionPointer(value);
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
		if (property == RETURN_TYPE_PROPERTY) {
			if (get) {
				return getReturnType();
			} else {
				setReturnType((Type) child);
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
		if (property == ARGUMENTS_PROPERTY) {
			return arguments();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return DELEGATE_TYPE;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		DelegateType result = new DelegateType(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setVariadic(isVariadic());
		result.setFunctionPointer(isFunctionPointer());
		result.setReturnType((Type) getReturnType().clone(target));
		result.arguments.addAll(ASTNode.copySubtrees(target, arguments()));
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
			acceptChild(visitor, getReturnType());
			acceptChildren(visitor, arguments);
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the variadic of this delegate type.
	 * 
	 * @return the variadic
	 */ 
	public boolean isVariadic() {
		return this.variadic;
	}

	/**
	 * Sets the variadic of this delegate type.
	 * 
	 * @param variadic the variadic
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setVariadic(boolean variadic) {
		preValueChange(VARIADIC_PROPERTY);
		this.variadic = variadic;
		postValueChange(VARIADIC_PROPERTY);
	}

	/**
	 * Returns the function pointer of this delegate type.
	 * 
	 * @return the function pointer
	 */ 
	public boolean isFunctionPointer() {
		return this.functionPointer;
	}

	/**
	 * Sets the function pointer of this delegate type.
	 * 
	 * @param functionPointer the function pointer
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setFunctionPointer(boolean functionPointer) {
		preValueChange(FUNCTION_POINTER_PROPERTY);
		this.functionPointer = functionPointer;
		postValueChange(FUNCTION_POINTER_PROPERTY);
	}

	/**
	 * Returns the return type of this delegate type.
	 * 
	 * @return the return type
	 */ 
	public Type getReturnType() {
		if (this.returnType == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.returnType == null) {
					preLazyInit();
					this.returnType = new PrimitiveType(this.ast);
					postLazyInit(this.returnType, RETURN_TYPE_PROPERTY);
				}
			}
		}
		return this.returnType;
	}

	/**
	 * Sets the return type of this delegate type.
	 * 
	 * @param returnType the return type
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setReturnType(Type returnType) {
		if (returnType == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.returnType;
		preReplaceChild(oldChild, returnType, RETURN_TYPE_PROPERTY);
		this.returnType = returnType;
		postReplaceChild(oldChild, returnType, RETURN_TYPE_PROPERTY);
	}

	/**
	 * Returns the live ordered list of arguments for this
	 * delegate type.
	 * 
	 * @return the live list of delegate type
	 *    (element type: <code>Argument</code>)
	 */ 
	public List<Argument> arguments() {
		return this.arguments;
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
			+ (this.returnType == null ? 0 : getReturnType().treeSize())
			+ (this.arguments.listSize())
	;
	}

}
