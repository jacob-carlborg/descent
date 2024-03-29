package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Template declaration AST node type.
 *
 * <pre>
 * TemplateDeclaration:
 *    <b>template</b> SimpleName <b>( [ TemplateParameter { <b>,</b> TemplateParameter } ]
 *    ( <b>if</b> <b>(</b> Expression <b>)</b> )
 *    <b>{</b> 
 *       { Declaration } 
 *    <b>}</b>
 * </pre>
 */
public class TemplateDeclaration extends Declaration {
	
	/**
	 * The "preDDocs" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor PRE_D_DOCS_PROPERTY =
	internalPreDDocsPropertyFactory(TemplateDeclaration.class); //$NON-NLS-1$

	/**
	 * The "modifiers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor MODIFIERS_PROPERTY =
	internalModifiersPropertyFactory(TemplateDeclaration.class); //$NON-NLS-1$

	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY =
		new ChildPropertyDescriptor(TemplateDeclaration.class, "name", SimpleName.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "templateParameters" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor TEMPLATE_PARAMETERS_PROPERTY =
		new ChildListPropertyDescriptor(TemplateDeclaration.class, "templateParameters", TemplateParameter.class, NO_CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * The "constraint" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor CONSTRAINT_PROPERTY =
		new ChildPropertyDescriptor(TemplateDeclaration.class, "constraint", Expression.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "declarations" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor DECLARATIONS_PROPERTY =
		new ChildListPropertyDescriptor(TemplateDeclaration.class, "declarations", Declaration.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "postDDoc" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POST_D_DOC_PROPERTY =
	internalPostDDocPropertyFactory(TemplateDeclaration.class); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(6);
		createPropertyList(TemplateDeclaration.class, properyList);
		addProperty(PRE_D_DOCS_PROPERTY, properyList);
		addProperty(MODIFIERS_PROPERTY, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(CONSTRAINT_PROPERTY, properyList);
		addProperty(TEMPLATE_PARAMETERS_PROPERTY, properyList);
		addProperty(DECLARATIONS_PROPERTY, properyList);
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
	 * The name.
	 */
	private SimpleName name;
	
	/**
	 * The constraint.
	 */
	private Expression constraint;

	/**
	 * The templateParameters
	 * (element type: <code>TemplateParameter</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList templateParameters =
		new ASTNode.NodeList(TEMPLATE_PARAMETERS_PROPERTY);
	/**
	 * The declarations
	 * (element type: <code>Declaration</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList declarations =
		new ASTNode.NodeList(DECLARATIONS_PROPERTY);

	/**
	 * Creates a new unparented template declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	TemplateDeclaration(AST ast) {
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
		if (property == CONSTRAINT_PROPERTY) {
			if (get) {
				return getConstraint();
			} else {
				setConstraint((Expression) child);
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
		if (property == TEMPLATE_PARAMETERS_PROPERTY) {
			return templateParameters();
		}
		if (property == DECLARATIONS_PROPERTY) {
			return declarations();
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
		final ChildPropertyDescriptor internalPostDDocProperty() {
			return POST_D_DOC_PROPERTY;
		}
		
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return TEMPLATE_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		TemplateDeclaration result = new TemplateDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.preDDocs.addAll(ASTNode.copySubtrees(target, preDDocs()));
		result.modifiers.addAll(ASTNode.copySubtrees(target, modifiers()));
		result.setName((SimpleName) getName().clone(target));
		result.setConstraint((Expression) ASTNode.copySubtree(target, getConstraint()));
		result.templateParameters.addAll(ASTNode.copySubtrees(target, templateParameters()));
		result.declarations.addAll(ASTNode.copySubtrees(target, declarations()));
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
			acceptChild(visitor, getName());
			acceptChild(visitor, getConstraint());
			acceptChildren(visitor, this.templateParameters);
			acceptChildren(visitor, this.declarations);
			acceptChild(visitor, getPostDDoc());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the name of this template declaration.
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
	 * Sets the name of this template declaration.
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
		preReplaceChild(oldChild, constraint, CONSTRAINT_PROPERTY);
		this.constraint = constraint;
		postReplaceChild(oldChild, constraint, CONSTRAINT_PROPERTY);
	}

	/**
	 * Returns the live ordered list of templateParameters for this
	 * template declaration.
	 * 
	 * @return the live list of template declaration
	 *    (element type: <code>TemplateParameter</code>)
	 */ 
	public List<TemplateParameter> templateParameters() {
		return this.templateParameters;
	}

	/**
	 * Returns the live ordered list of declarations for this
	 * template declaration.
	 * 
	 * @return the live list of template declaration
	 *    (element type: <code>Declaration</code>)
	 */ 
	public List<Declaration> declarations() {
		return this.declarations;
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
			+ (this.preDDocs.listSize())
			+ (this.modifiers.listSize())
			+ (this.name == null ? 0 : getName().treeSize())
			+ (this.constraint == null ? 0 : getConstraint().treeSize())
			+ (this.templateParameters.listSize())
			+ (this.declarations.listSize())
			+ (this.postDDoc == null ? 0 : getPostDDoc().treeSize())
	;
	}
	
	/**
	 * Resolves and returns the binding for the type declared in this template
	 * declaration.
	 * <p>
	 * Note that bindings are generally unavailable unless requested when the
	 * AST is being built.
	 * </p>
	 * 
	 * @return the binding, or <code>null</code> if the binding cannot be 
	 *    resolved
	 */	
	public final ITypeBinding resolveBinding() {
		return this.ast.getBindingResolver().resolveTemplate(this);
	}

}
