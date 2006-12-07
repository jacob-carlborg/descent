package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IFunctionDeclaration;

// TODO comment 
public class FunctionDeclaration extends Declaration implements IFunctionDeclaration {
	
	public enum Kind {
		FUNCTION,
		CONSTRUCTOR,
		DESTRUCTOR,
		STATIC_CONSTRUCTOR,
		STATIC_DESTRUCTOR,
		NEW,
		DELETE
	}

	/**
	 * The "modifierFlags" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor MODIFIER_FLAGS_PROPERTY =
		new SimplePropertyDescriptor(FunctionDeclaration.class, "modifierFlags", int.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "kind" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor KIND_PROPERTY =
		new SimplePropertyDescriptor(FunctionDeclaration.class, "kind", Kind.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "returnType" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor RETURN_TYPE_PROPERTY =
		new ChildPropertyDescriptor(FunctionDeclaration.class, "returnType", Type.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY =
		new ChildPropertyDescriptor(FunctionDeclaration.class, "name", SimpleName.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "templateParameters" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor TEMPLATE_PARAMETERS_PROPERTY =
		new ChildListPropertyDescriptor(FunctionDeclaration.class, "templateParameters", TemplateParameter.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "arguments" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor ARGUMENTS_PROPERTY =
		new ChildListPropertyDescriptor(FunctionDeclaration.class, "arguments", Argument.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "variadic" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor VARIADIC_PROPERTY =
		new SimplePropertyDescriptor(FunctionDeclaration.class, "variadic", boolean.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "precondition" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor PRECONDITION_PROPERTY =
		new ChildPropertyDescriptor(FunctionDeclaration.class, "precondition", Statement.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "postcondition" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POSTCONDITION_PROPERTY =
		new ChildPropertyDescriptor(FunctionDeclaration.class, "postcondition", Statement.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "postconditionVariableName" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POSTCONDITIONVARIABLENAME_PROPERTY =
		new ChildPropertyDescriptor(FunctionDeclaration.class, "postconditionVariableName", SimpleName.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "body" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor BODY_PROPERTY =
		new ChildPropertyDescriptor(FunctionDeclaration.class, "body", Statement.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(11);
		createPropertyList(FunctionDeclaration.class, properyList);
		addProperty(MODIFIER_FLAGS_PROPERTY, properyList);
		addProperty(KIND_PROPERTY, properyList);
		addProperty(RETURN_TYPE_PROPERTY, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(TEMPLATE_PARAMETERS_PROPERTY, properyList);
		addProperty(ARGUMENTS_PROPERTY, properyList);
		addProperty(VARIADIC_PROPERTY, properyList);
		addProperty(PRECONDITION_PROPERTY, properyList);
		addProperty(POSTCONDITION_PROPERTY, properyList);
		addProperty(POSTCONDITIONVARIABLENAME_PROPERTY, properyList);
		addProperty(BODY_PROPERTY, properyList);
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
	 * The modifierFlags.
	 */
	private int modifierFlags;

	/**
	 * The kind.
	 */
	private Kind kind;

	/**
	 * The returnType.
	 */
	private Type returnType;

	/**
	 * The name.
	 */
	private SimpleName name;

	/**
	 * The template parameters
	 * (element type: <code>TemplateParameter</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList templateParameters =
		new ASTNode.NodeList(TEMPLATE_PARAMETERS_PROPERTY);
	/**
	 * The arguments
	 * (element type: <code>Argument</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList arguments =
		new ASTNode.NodeList(ARGUMENTS_PROPERTY);
	/**
	 * The variadic.
	 */
	private boolean variadic;

	/**
	 * The precondition.
	 */
	private Statement precondition;

	/**
	 * The postcondition.
	 */
	private Statement postcondition;

	/**
	 * The postconditionVariableName.
	 */
	private SimpleName postconditionVariableName;

	/**
	 * The body.
	 */
	private Statement body;


	/**
	 * Creates a new unparented function declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	FunctionDeclaration(AST ast) {
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
		if (property == KIND_PROPERTY) {
			if (get) {
				return getKind();
			} else {
				setKind((Kind) value);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetObjectProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int internalGetSetIntProperty(SimplePropertyDescriptor property, boolean get, int value) {
		if (property == MODIFIER_FLAGS_PROPERTY) {
			if (get) {
				return getModifierFlags();
			} else {
				setModifierFlags(value);
				return 0;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetIntProperty(property, get, value);
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
		if (property == NAME_PROPERTY) {
			if (get) {
				return getName();
			} else {
				setName((SimpleName) child);
				return null;
			}
		}
		if (property == PRECONDITION_PROPERTY) {
			if (get) {
				return getPrecondition();
			} else {
				setPrecondition((Statement) child);
				return null;
			}
		}
		if (property == POSTCONDITION_PROPERTY) {
			if (get) {
				return getPostcondition();
			} else {
				setPostcondition((Statement) child);
				return null;
			}
		}
		if (property == POSTCONDITIONVARIABLENAME_PROPERTY) {
			if (get) {
				return getPostconditionVariableName();
			} else {
				setPostconditionVariableName((SimpleName) child);
				return null;
			}
		}
		if (property == BODY_PROPERTY) {
			if (get) {
				return getBody();
			} else {
				setBody((Statement) child);
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
		if (property == TEMPLATE_PARAMETERS_PROPERTY) {
			return templateParameters();
		}
		if (property == ARGUMENTS_PROPERTY) {
			return arguments();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return FUNCTION_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		FunctionDeclaration result = new FunctionDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setModifierFlags(getModifierFlags());
		result.setKind(getKind());
		result.setReturnType((Type) getReturnType().clone(target));
		result.setName((SimpleName) getName().clone(target));
		result.templateParameters.addAll(ASTNode.copySubtrees(target, templateParameters()));
		result.arguments.addAll(ASTNode.copySubtrees(target, arguments()));
		result.setVariadic(isVariadic());
	result.setPrecondition((Statement) ASTNode.copySubtree(target, getPrecondition()));
	result.setPostcondition((Statement) ASTNode.copySubtree(target, getPostcondition()));
	result.setPostconditionVariableName((SimpleName) ASTNode.copySubtree(target, getPostconditionVariableName()));
		result.setBody((Statement) getBody().clone(target));
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
			acceptChild(visitor, getName());
			acceptChildren(visitor, templateParameters());
			acceptChildren(visitor, arguments());
			acceptChild(visitor, getPrecondition());
			acceptChild(visitor, getPostcondition());
			acceptChild(visitor, getPostconditionVariableName());
			acceptChild(visitor, getBody());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the modifier flags of this function declaration.
	 * 
	 * @return the modifier flags
	 */ 
	public int getModifierFlags() {
		return this.modifierFlags;
	}

	/**
	 * Sets the modifier flags of this function declaration.
	 * 
	 * @param modifierFlags the modifier flags
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setModifierFlags(int modifierFlags) {
		preValueChange(MODIFIER_FLAGS_PROPERTY);
		this.modifierFlags = modifierFlags;
		postValueChange(MODIFIER_FLAGS_PROPERTY);
	}

	/**
	 * Returns the kind of this function declaration.
	 * 
	 * @return the kind
	 */ 
	public Kind getKind() {
		return this.kind;
	}

	/**
	 * Sets the kind of this function declaration.
	 * 
	 * @param kind the kind
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setKind(Kind kind) {
		if (kind == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(KIND_PROPERTY);
		this.kind = kind;
		postValueChange(KIND_PROPERTY);
	}

	/**
	 * Returns the return type of this function declaration.
	 * 
	 * @return the return type
	 */ 
	public Type getReturnType() {
		if (this.returnType == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.returnType == null) {
					preLazyInit();
					this.returnType = Type.tvoid;
					postLazyInit(this.returnType, RETURN_TYPE_PROPERTY);
				}
			}
		}
		return this.returnType;
	}

	/**
	 * Sets the return type of this function declaration.
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
	 * Returns the name of this function declaration.
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
	 * Sets the name of this function declaration.
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
	 * Returns the live ordered list of template parameters for this
	 * function declaration.
	 * 
	 * @return the live list of function declaration
	 *    (element type: <code>TemplateParameter</code>)
	 */ 
	public List<TemplateParameter> templateParameters() {
		return this.templateParameters;
	}

	/**
	 * Returns the live ordered list of arguments for this
	 * function declaration.
	 * 
	 * @return the live list of function declaration
	 *    (element type: <code>Argument</code>)
	 */ 
	public List<Argument> arguments() {
		return this.arguments;
	}

	/**
	 * Returns the variadic of this function declaration.
	 * 
	 * @return the variadic
	 */ 
	public boolean isVariadic() {
		return this.variadic;
	}

	/**
	 * Sets the variadic of this function declaration.
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
	 * Returns the precondition of this function declaration.
	 * 
	 * @return the precondition
	 */ 
	public Statement getPrecondition() {
		return this.precondition;
	}

	/**
	 * Sets the precondition of this function declaration.
	 * 
	 * @param precondition the precondition
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setPrecondition(Statement precondition) {
		ASTNode oldChild = this.precondition;
		preReplaceChild(oldChild, precondition, PRECONDITION_PROPERTY);
		this.precondition = precondition;
		postReplaceChild(oldChild, precondition, PRECONDITION_PROPERTY);
	}

	/**
	 * Returns the postcondition of this function declaration.
	 * 
	 * @return the postcondition
	 */ 
	public Statement getPostcondition() {
		return this.postcondition;
	}

	/**
	 * Sets the postcondition of this function declaration.
	 * 
	 * @param postcondition the postcondition
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setPostcondition(Statement postcondition) {
		ASTNode oldChild = this.postcondition;
		preReplaceChild(oldChild, postcondition, POSTCONDITION_PROPERTY);
		this.postcondition = postcondition;
		postReplaceChild(oldChild, postcondition, POSTCONDITION_PROPERTY);
	}

	/**
	 * Returns the postconditionVariableName of this function declaration.
	 * 
	 * @return the postconditionVariableName
	 */ 
	public SimpleName getPostconditionVariableName() {
		return this.postconditionVariableName;
	}

	/**
	 * Sets the postconditionVariableName of this function declaration.
	 * 
	 * @param postconditionVariableName the postconditionVariableName
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setPostconditionVariableName(SimpleName postconditionVariableName) {
		ASTNode oldChild = this.postconditionVariableName;
		preReplaceChild(oldChild, postconditionVariableName, POSTCONDITIONVARIABLENAME_PROPERTY);
		this.postconditionVariableName = postconditionVariableName;
		postReplaceChild(oldChild, postconditionVariableName, POSTCONDITIONVARIABLENAME_PROPERTY);
	}

	/**
	 * Returns the body of this function declaration.
	 * 
	 * @return the body
	 */ 
	public Statement getBody() {
		if (this.body == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.body == null) {
					preLazyInit();
					this.body = new Block(this.ast);
					postLazyInit(this.body, BODY_PROPERTY);
				}
			}
		}
		return this.body;
	}

	/**
	 * Sets the body of this function declaration.
	 * 
	 * @param body the body
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setBody(Statement body) {
		if (body == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.body;
		preReplaceChild(oldChild, body, BODY_PROPERTY);
		this.body = body;
		postReplaceChild(oldChild, body, BODY_PROPERTY);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 11 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.returnType == null ? 0 : getReturnType().treeSize())
			+ (this.name == null ? 0 : getName().treeSize())
			+ (this.templateParameters.listSize())
			+ (this.arguments.listSize())
			+ (this.precondition == null ? 0 : getPrecondition().treeSize())
			+ (this.postcondition == null ? 0 : getPostcondition().treeSize())
			+ (this.postconditionVariableName == null ? 0 : getPostconditionVariableName().treeSize())
			+ (this.body == null ? 0 : getBody().treeSize())
	;
	}
	
}
