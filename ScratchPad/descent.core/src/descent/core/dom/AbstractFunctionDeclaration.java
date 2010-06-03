package descent.core.dom;

import java.util.List;

/**
 * Abstract subclass for function declarations.
 */
public abstract class AbstractFunctionDeclaration extends Declaration
	implements ITemplateFunctionDeclaration {
	
	/**
	 * The arguments
	 * (element type: <code>Argument</code>).
	 * Defaults to an empty list.
	 */
	final ASTNode.NodeList arguments =
		new ASTNode.NodeList(getArgumentsProperty());

	/**
	 * The template parameters
	 * (element type: <code>TemplateParameter</code>).
	 * Defaults to an empty list.
	 */
	final ASTNode.NodeList templateParameters =
		new ASTNode.NodeList(getTemplateParametersProperty());
	
	/**
	 * The constraint.
	 */
	Expression constraint;
	
	/**
	 * Is the function variadic?
	 */
	boolean variadic;
	
	/**
	 * The precondition.
	 */
	Block precondition;
	
	/**
	 * The postcondition.
	 */
	Block postcondition;
	
	/**
	 * The postcondition variable name.
	 */
	SimpleName postconditionVariableName;
	
	/**
	 * The optional body.
	 */
	Block body;
	
	/**
	 * Returns structural property descriptor for the "templateParameters" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildListPropertyDescriptor internalTemplateParametersProperty();
	
	/**
	 * Returns structural property descriptor for the "constraint" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildPropertyDescriptor internalConstraintPropertyFactory();
	
	/**
	 * Returns structural property descriptor for the "arguments" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildListPropertyDescriptor internalArgumentsProperty();
	
	/**
	 * Returns structural property descriptor for the "variadic" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract SimplePropertyDescriptor internalVariadicProperty();
	
	/**
	 * Returns structural property descriptor for the "precondition" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildPropertyDescriptor internalPreconditionProperty();
	
	/**
	 * Returns structural property descriptor for the "postcondition" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildPropertyDescriptor internalPostconditionProperty();
	
	/**
	 * Returns structural property descriptor for the "postconditionVariableName" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildPropertyDescriptor internalPostconditionVariableNameProperty();
	
	/**
	 * Returns structural property descriptor for the "body" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildPropertyDescriptor internalBodyProperty();
	
	/**
	 * Returns structural property descriptor for the "templateParameters" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final ChildListPropertyDescriptor getTemplateParametersProperty() {
		return internalTemplateParametersProperty();
	}
	
	/**
	 * Returns structural property descriptor for the "constraint" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final ChildPropertyDescriptor getConstraintProperty() {
		return internalBodyProperty();
	}
	
	/**
	 * Returns structural property descriptor for the "arguments" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final ChildListPropertyDescriptor getArgumentsProperty() {
		return internalArgumentsProperty();
	}
	
	/**
	 * Returns structural property descriptor for the "variadic" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final SimplePropertyDescriptor getVariadicProperty() {
		return internalVariadicProperty();
	}
	
	/**
	 * Returns structural property descriptor for the "precondition" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final ChildPropertyDescriptor getPreconditionProperty() {
		return internalPreconditionProperty();
	}
	
	/**
	 * Returns structural property descriptor for the "precondition" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final ChildPropertyDescriptor getPostconditionProperty() {
		return internalPostconditionProperty();
	}
	
	/**
	 * Returns structural property descriptor for the "postconditionVariableName" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final ChildPropertyDescriptor getPostconditionVariableNameProperty() {
		return internalPostconditionVariableNameProperty();
	}
	
	/**
	 * Returns structural property descriptor for the "body" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final ChildPropertyDescriptor getBodyProperty() {
		return internalBodyProperty();
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "templateParameters" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildListPropertyDescriptor internalTemplateParametersPropertyFactory(Class nodeClass) {
		return new ChildListPropertyDescriptor(nodeClass, "templateParameters", TemplateParameter.class, NO_CYCLE_RISK); //$NON-NLS-1$
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "constraint" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildPropertyDescriptor internalConstraintPropertyFactory(Class nodeClass) {
		return new ChildPropertyDescriptor(nodeClass, "constraint", Block.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "arguments" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildListPropertyDescriptor internalArgumentsPropertyFactory(Class nodeClass) {
		return new ChildListPropertyDescriptor(nodeClass, "arguments", Argument.class, NO_CYCLE_RISK); //$NON-NLS-1$
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "variadic" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final SimplePropertyDescriptor internalVariadicPropertyFactory(Class nodeClass) {
		return new SimplePropertyDescriptor(nodeClass, "variadic", boolean.class, MANDATORY); //$NON-NLS-1$
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "precondition" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildPropertyDescriptor internalPreconditionPropertyFactory(Class nodeClass) {
		return new ChildPropertyDescriptor(nodeClass, "precondition", Block.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "postcondition" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildPropertyDescriptor internalPostconditionPropertyFactory(Class nodeClass) {
		return new ChildPropertyDescriptor(nodeClass, "postcondition", Block.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "postconditionVariableName" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildPropertyDescriptor internalPostconditionVariableNamePropertyFactory(Class nodeClass) {
		return new ChildPropertyDescriptor(nodeClass, "postconditionVariableName", SimpleName.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "body" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildPropertyDescriptor internalBodyPropertyFactory(Class nodeClass) {
		return new ChildPropertyDescriptor(nodeClass, "body", Block.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$
	}
	
	/**
	 * Creates a new AST node for an abstract function declaration.
	 * <p>
	 * N.B. This constructor is package-private; all subclasses must be 
	 * declared in the same package; clients are unable to declare 
	 * additional subclasses.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	AbstractFunctionDeclaration(AST ast) {
		super(ast);
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
	 * Returns the constraint of this template declaration.
	 * 
	 * @return the constraint
	 */ 
	public Expression getConstraint() {
		return this.constraint;
	}

	/**
	 * Sets the constraint of this template declaration.
	 * 
	 * @param constraint the constraint
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setConstraint(Expression constraint) {
		ASTNode oldChild = this.constraint;
		preReplaceChild(oldChild, constraint, getConstraintProperty());
		this.constraint = constraint;
		postReplaceChild(oldChild, constraint, getConstraintProperty());
	}
	
	/**
	 * Returns the live ordered list of arguments for this
	 * declaration.
	 * 
	 * @return the live list of arguments
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
		preValueChange(getVariadicProperty());
		this.variadic = variadic;
		postValueChange(getVariadicProperty());
	}
	
	/**
	 * Returns the precondition of this function declaration.
	 * 
	 * @return the precondition
	 */ 
	public Block getPrecondition() {
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
	public void setPrecondition(Block precondition) {
		ASTNode oldChild = this.precondition;
		preReplaceChild(oldChild, precondition, getPreconditionProperty());
		this.precondition = precondition;
		postReplaceChild(oldChild, precondition, getPreconditionProperty());
	}

	/**
	 * Returns the postcondition of this function declaration.
	 * 
	 * @return the postcondition
	 */ 
	public Block getPostcondition() {
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
	public void setPostcondition(Block postcondition) {
		ASTNode oldChild = this.postcondition;
		preReplaceChild(oldChild, postcondition, getPostconditionProperty());
		this.postcondition = postcondition;
		postReplaceChild(oldChild, postcondition, getPostconditionProperty());
	}

	/**
	 * Returns the postcondition variable name of this function declaration.
	 * 
	 * @return the postcondition variable name
	 */ 
	public SimpleName getPostconditionVariableName() {
		return this.postconditionVariableName;
	}

	/**
	 * Sets the postcondition variable name of this function declaration.
	 * 
	 * @param postconditionVariableName the postcondition variable name
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setPostconditionVariableName(SimpleName postconditionVariableName) {
		ASTNode oldChild = this.postconditionVariableName;
		preReplaceChild(oldChild, postconditionVariableName, getPostconditionVariableNameProperty());
		this.postconditionVariableName = postconditionVariableName;
		postReplaceChild(oldChild, postconditionVariableName, getPostconditionVariableNameProperty());
	}

	/**
	 * Returns the body of this function declaration.
	 * 
	 * @return the body
	 */ 
	public Block getBody() {
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
	public void setBody(Block body) {
		ASTNode oldChild = this.body;
		preReplaceChild(oldChild, body, getBodyProperty());
		this.body = body;
		postReplaceChild(oldChild, body, getBodyProperty());
	}
	
	/**
	 * Returns true if this AbstractFunctionDeclaration is a
	 * ConstructorDeclaration.
	 */
	public boolean isConstructor() {
		return this instanceof ConstructorDeclaration;
	}
	
	/**
	 * Returns true if this AbstractFunctionDeclaration is a
	 * FunctionDeclaration.
	 */
	public boolean isFunction() {
		return this instanceof FunctionDeclaration;
	}

}
