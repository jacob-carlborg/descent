package descent.core.dom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Function declaration AST node.
 * 
 * <pre>
 * FunctionDeclaration:
 *    Type SimpleName [ <b>(</b> TemplateParameter { <b>,</b> TemplateParameter } <b>)</b> ]
 *       <b>(</b> [ Argument { <b>,</b> Argument } ] <b>)</b>
 *       { Modifiers }
 *       [ <b>in</b> Block ]
 *       [ <b>out</b> [ <b>(</b> SimpleName <b>)</b> ] Block ]
 *       [ <b>body</b> ] Block
 * </pre>
 */
public class FunctionDeclaration extends AbstractFunctionDeclaration {
	
	/**
	 * The "preDDocs" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor PRE_D_DOCS_PROPERTY =
	internalPreDDocsPropertyFactory(FunctionDeclaration.class); //$NON-NLS-1$

	/**
	 * The "modifiers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor MODIFIERS_PROPERTY =
	internalModifiersPropertyFactory(FunctionDeclaration.class); //$NON-NLS-1$
	
	/**
	 * The "postModifiers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor POST_MODIFIERS_PROPERTY =
		new ChildListPropertyDescriptor(FunctionDeclaration.class, "postModifiers", Modifier.class, NO_CYCLE_RISK); //$NON-NLS-1$

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
	internalArgumentsPropertyFactory(FunctionDeclaration.class); //$NON-NLS-1$

	/**
	 * The "variadic" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor VARIADIC_PROPERTY =
	internalVariadicPropertyFactory(FunctionDeclaration.class); //$NON-NLS-1$

	/**
	 * The "precondition" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor PRECONDITION_PROPERTY =
	internalPreconditionPropertyFactory(FunctionDeclaration.class); //$NON-NLS-1$

	/**
	 * The "postcondition" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POSTCONDITION_PROPERTY =
	internalPostconditionPropertyFactory(FunctionDeclaration.class); //$NON-NLS-1$

	/**
	 * The "postconditionVariableName" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POSTCONDITION_VARIABLE_NAME_PROPERTY =
	internalPostconditionVariableNamePropertyFactory(FunctionDeclaration.class); //$NON-NLS-1$

	/**
	 * The "body" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor BODY_PROPERTY =
	internalBodyPropertyFactory(FunctionDeclaration.class); //$NON-NLS-1$

	/**
	 * The "postDDoc" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POST_D_DOC_PROPERTY =
	internalPostDDocPropertyFactory(FunctionDeclaration.class); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(12);
		createPropertyList(FunctionDeclaration.class, properyList);
		addProperty(PRE_D_DOCS_PROPERTY, properyList);
		addProperty(MODIFIERS_PROPERTY, properyList);
		addProperty(RETURN_TYPE_PROPERTY, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(TEMPLATE_PARAMETERS_PROPERTY, properyList);
		addProperty(ARGUMENTS_PROPERTY, properyList);
		addProperty(VARIADIC_PROPERTY, properyList);
		addProperty(POST_MODIFIERS_PROPERTY, properyList);
		addProperty(PRECONDITION_PROPERTY, properyList);
		addProperty(POSTCONDITION_PROPERTY, properyList);
		addProperty(POSTCONDITION_VARIABLE_NAME_PROPERTY, properyList);
		addProperty(BODY_PROPERTY, properyList);
		addProperty(POST_D_DOC_PROPERTY, properyList);
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
	 * The post modifiers
	 * (element type: <code>Modifier</code>).
	 * Defaults to an empty list.
	 */
	final ASTNode.NodeList postModifiers =
		new ASTNode.NodeList(POST_MODIFIERS_PROPERTY);

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
				setPrecondition((Block) child);
				return null;
			}
		}
		if (property == POSTCONDITION_PROPERTY) {
			if (get) {
				return getPostcondition();
			} else {
				setPostcondition((Block) child);
				return null;
			}
		}
		if (property == POSTCONDITION_VARIABLE_NAME_PROPERTY) {
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
				setBody((Block) child);
				return null;
			}
		}
		if (property == POST_D_DOC_PROPERTY) {
			if (get) {
				return getPostDDoc();
			} else {
				setPostDDoc((DDocComment) child);
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
		if (property == PRE_D_DOCS_PROPERTY) {
			return preDDocs();
		}
		if (property == MODIFIERS_PROPERTY) {
			return modifiers();
		}
		if (property == POST_MODIFIERS_PROPERTY) {
			return modifiers();
		}
		if (property == TEMPLATE_PARAMETERS_PROPERTY) {
			return templateParameters();
		}
		if (property == ARGUMENTS_PROPERTY) {
			return arguments();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	@Override
	final ChildListPropertyDescriptor internalPreDDocsProperty() {
		return PRE_D_DOCS_PROPERTY;
	}
	
	@Override
	final ChildListPropertyDescriptor internalModifiersProperty() {
		return MODIFIERS_PROPERTY;
	}
	
	@Override
	final ChildListPropertyDescriptor internalArgumentsProperty() {
		return ARGUMENTS_PROPERTY;
	}
	
	@Override
	final SimplePropertyDescriptor internalVariadicProperty() {
		return VARIADIC_PROPERTY;
	}
	
	@Override
	final ChildPropertyDescriptor internalPreconditionProperty() {
		return PRECONDITION_PROPERTY;
	}
	
	@Override
	final ChildPropertyDescriptor internalPostconditionProperty() {
		return POSTCONDITION_PROPERTY;
	}
	
	@Override
	final ChildPropertyDescriptor internalPostconditionVariableNameProperty() {
		return POSTCONDITION_VARIABLE_NAME_PROPERTY;
	}
	
	@Override
	final ChildPropertyDescriptor internalBodyProperty() {
		return BODY_PROPERTY;
	}
	
	@Override
	final ChildPropertyDescriptor internalPostDDocProperty() {
		return POST_D_DOC_PROPERTY;
	}
		
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return FUNCTION_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		FunctionDeclaration result = new FunctionDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.preDDocs.addAll(ASTNode.copySubtrees(target, preDDocs()));
		result.modifiers.addAll(ASTNode.copySubtrees(target, modifiers()));
		result.postModifiers.addAll(ASTNode.copySubtrees(target, postModifiers()));
		result.setReturnType((Type) getReturnType().clone(target));
		result.setName((SimpleName) getName().clone(target));
		result.templateParameters.addAll(ASTNode.copySubtrees(target, templateParameters()));
		result.arguments.addAll(ASTNode.copySubtrees(target, arguments()));
		result.setVariadic(isVariadic());
		result.setPrecondition((Block) ASTNode.copySubtree(target, getPrecondition()));
		result.setPostcondition((Block) ASTNode.copySubtree(target, getPostcondition()));
		result.setPostconditionVariableName((SimpleName) ASTNode.copySubtree(target, getPostconditionVariableName()));
		result.setBody((Block) getBody().clone(target));
		result.setPostDDoc((DDocComment) ASTNode.copySubtree(target, getPostDDoc()));
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
			acceptChildren(visitor, this.preDDocs);
			acceptChildren(visitor, this.modifiers);
			acceptChild(visitor, getReturnType());
			acceptChild(visitor, getName());
			acceptChildren(visitor, this.templateParameters);
			acceptChildren(visitor, this.arguments);
			acceptChildren(visitor, this.postModifiers);
			acceptChild(visitor, getPrecondition());
			acceptChild(visitor, getPostcondition());
			acceptChild(visitor, getPostconditionVariableName());
			acceptChild(visitor, getBody());
			acceptChild(visitor, getPostDDoc());
		}
		visitor.endVisit(this);
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
					this.returnType = new PrimitiveType(this.ast);
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
	 * Returns the live ordered list of post modifiers for this
	 * declaration.
	 * 
	 * @return the live list of post modifiers
	 *    (element type: <code>Modifier</code>)
	 */ 
	public final List<Modifier> postModifiers() {
		return this.postModifiers;
	}
	
	/**
	 * Returns the modifiers explicitly specified on this declaration.
	 * 
	 * @return the bit-wise or of <code>Modifier</code> constants
	 * @see Modifier
	 */
	@Override
	public int getModifiers() {
		int computedmodifierFlags = super.getModifiers();
		for (Iterator it = postModifiers().iterator(); it.hasNext(); ) {
			Object x = it.next();
			if (x instanceof Modifier) {
				computedmodifierFlags |= ((Modifier) x).getModifierKeyword().toFlagValue();
			}
		}
		return computedmodifierFlags;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 13 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.preDDocs.listSize())
			+ (this.modifiers.listSize())
			+ (this.postModifiers.listSize())
			+ (this.returnType == null ? 0 : getReturnType().treeSize())
			+ (this.name == null ? 0 : getName().treeSize())
			+ (this.templateParameters.listSize())
			+ (this.arguments.listSize())
			+ (this.precondition == null ? 0 : getPrecondition().treeSize())
			+ (this.postcondition == null ? 0 : getPostcondition().treeSize())
			+ (this.postconditionVariableName == null ? 0 : getPostconditionVariableName().treeSize())
			+ (this.body == null ? 0 : getBody().treeSize())
			+ (this.postDDoc == null ? 0 : getPostDDoc().treeSize())
	;
	}
	
	/**
	 * Resolves and returns the binding for the function declared
	 * in this function declaration.
	 * <p>
	 * Note that bindings are generally unavailable unless requested when the
	 * AST is being built.
	 * </p>
	 * 
	 * @return the binding, or <code>null</code> if the binding cannot be 
	 *    resolved
	 */	
	public IMethodBinding resolveBinding() {
		return this.ast.getBindingResolver().resolveMethod(this);
	}
	
}
