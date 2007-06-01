package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * An expression that declares an annonymous function.
 * TODO comment better, Syntax as well
 */
public class FunctionLiteralDeclarationExpression extends Expression
		implements IFunctionDeclaration
{
	
	public static enum Syntax {
		EMPTY,
		FUNCTION,
		DELEGATE
	}
	
	/**
	 * The "syntax" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor SYNTAX_PROPERTY =
		new SimplePropertyDescriptor(FunctionLiteralDeclarationExpression.class, "syntax", Syntax.class, OPTIONAL); //$NON-NLS-1$
	
	/**
	 * The "returnType" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor RETURN_TYPE_PROPERTY =
		new ChildPropertyDescriptor(FunctionLiteralDeclarationExpression.class, "returnType", Type.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "arguments" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor ARGUMENTS_PROPERTY =
		new ChildListPropertyDescriptor(FunctionLiteralDeclarationExpression.class, "arguments", Argument.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "variadic" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor VARIADIC_PROPERTY =
		new SimplePropertyDescriptor(FunctionLiteralDeclarationExpression.class, "variadic", boolean.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "precondition" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor PRECONDITION_PROPERTY =
		new ChildPropertyDescriptor(FunctionLiteralDeclarationExpression.class, "precondition", Statement.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "postcondition" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POSTCONDITION_PROPERTY =
		new ChildPropertyDescriptor(FunctionLiteralDeclarationExpression.class, "postcondition", Statement.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "postconditionVariableName" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POSTCONDITIONVARIABLENAME_PROPERTY =
		new ChildPropertyDescriptor(FunctionLiteralDeclarationExpression.class, "postconditionVariableName", SimpleName.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "body" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor BODY_PROPERTY =
		new ChildPropertyDescriptor(FunctionLiteralDeclarationExpression.class, "body", Statement.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(7);
		createPropertyList(FunctionLiteralDeclarationExpression.class, properyList);
		addProperty(SYNTAX_PROPERTY, properyList);
		addProperty(RETURN_TYPE_PROPERTY, properyList);
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
	 * The syntax.
	 */
	private Syntax syntax;
	
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
	 * Creates a new unparented function literal declaration expression node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	FunctionLiteralDeclarationExpression(AST ast) {
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
		if (property == SYNTAX_PROPERTY) {
			if (get) {
				return getSyntax();
			} else {
				setSyntax((Syntax) value);
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
		return FUNCTION_LITERAL_DECLARATION_EXPRESSION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		FunctionLiteralDeclarationExpression result = new FunctionLiteralDeclarationExpression(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setSyntax(getSyntax());
		result.setReturnType((Type) getReturnType().clone(target));
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
			acceptChildren(visitor, arguments);
			acceptChild(visitor, getPrecondition());
			acceptChild(visitor, getPostcondition());
			acceptChild(visitor, getPostconditionVariableName());
			acceptChild(visitor, getBody());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the syntax of this function literal declaration expression.
	 * 
	 * @return the syntax
	 */ 
	public Syntax getSyntax() {
		return this.syntax;
	}

	/**
	 * Sets the syntax of this function literal declaration expression.
	 * 
	 * @param syntax the syntax
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setSyntax(Syntax syntax) {
		if (syntax == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(SYNTAX_PROPERTY);
		this.syntax = syntax;
		postValueChange(SYNTAX_PROPERTY);
	}
	
	/**
	 * Returns the return type of this function declaration.
	 * 
	 * @return the return type
	 */ 
	public Type getReturnType() {
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
		ASTNode oldChild = this.returnType;
		preReplaceChild(oldChild, returnType, RETURN_TYPE_PROPERTY);
		this.returnType = returnType;
		postReplaceChild(oldChild, returnType, RETURN_TYPE_PROPERTY);
	}

	/**
	 * Returns the live ordered list of arguments for this
	 * function literal declaration expression.
	 * 
	 * @return the live list of function literal declaration expression
	 *    (element type: <code>Argument</code>)
	 */ 
	public List<Argument> arguments() {
		return this.arguments;
	}

	/**
	 * Returns the variadic of this function literal declaration expression.
	 * 
	 * @return the variadic
	 */ 
	public boolean isVariadic() {
		return this.variadic;
	}

	/**
	 * Sets the variadic of this function literal declaration expression.
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
	 * Returns the precondition of this function literal declaration expression.
	 * 
	 * @return the precondition
	 */ 
	public Statement getPrecondition() {
		return this.precondition;
	}

	/**
	 * Sets the precondition of this function literal declaration expression.
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
	 * Returns the postcondition of this function literal declaration expression.
	 * 
	 * @return the postcondition
	 */ 
	public Statement getPostcondition() {
		return this.postcondition;
	}

	/**
	 * Sets the postcondition of this function literal declaration expression.
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
	 * Returns the postconditionVariableName of this function literal declaration expression.
	 * 
	 * @return the postconditionVariableName
	 */ 
	public SimpleName getPostconditionVariableName() {
		return this.postconditionVariableName;
	}

	/**
	 * Sets the postconditionVariableName of this function literal declaration expression.
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
	 * Returns the body of this function literal declaration expression.
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
	 * Sets the body of this function literal declaration expression.
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
		return BASE_NODE_SIZE + 7 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.returnType == null ? 0 : getReturnType().treeSize())
			+ (this.arguments.listSize())
			+ (this.precondition == null ? 0 : getPrecondition().treeSize())
			+ (this.postcondition == null ? 0 : getPostcondition().treeSize())
			+ (this.postconditionVariableName == null ? 0 : getPostconditionVariableName().treeSize())
			+ (this.body == null ? 0 : getBody().treeSize())
	;
	}

}
