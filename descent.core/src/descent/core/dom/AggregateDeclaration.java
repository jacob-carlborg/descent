package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate declaration AST node. An aggregate can be a class, interface, struct or union.
 * 
 * <pre>
 * AggregateDeclaration:
 *    { Modifier } 
 *       [<b>class</b> | <b>struct</b> | <b>union</b> | <b>interface</b> ] SimpleName 
 *          [ <b>(</b> TemplateParameter { <b>,</b> TemplateParameter } <b>)</b> ]
 *          [ <b>:</b> Type { <b>,</b> Type } ]
 *       <b>{</b>
 *          { Declaration }
 *       <b>}</b> *    
 * </pre>
 */
public class AggregateDeclaration extends Declaration {
	
	/**
	 * The kind of declaration.
	 */
	public static enum Kind {
		/** Class declaration */
		CLASS("class"),
		/** Interface declaration */
		INTERFACE("interface"),
		/** "inout" passage mode */
		STRUCT("struct"),
		/** "lazy" passage mode */
		UNION("union")
		
		;
		
		String token;
		
		Kind(String token) {
			this.token = token;
		}
		
		/**
		 * Returns the token of this kind.
		 */
		public String getToken() {
			return token;
		}
		
		@Override
		public String toString() {
			return token;
		}
		
	}

	/**
	 * The "preDDocs" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor PRE_D_DOCS_PROPERTY =
	internalPreDDocsPropertyFactory(AggregateDeclaration.class); //$NON-NLS-1$

	/**
	 * The "modifiers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor MODIFIERS_PROPERTY =
	internalModifiersPropertyFactory(AggregateDeclaration.class); //$NON-NLS-1$

	/**
	 * The "kind" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor KIND_PROPERTY =
		new SimplePropertyDescriptor(AggregateDeclaration.class, "kind", Kind.class, MANDATORY); //$NON-NLS-1$

	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY =
		new ChildPropertyDescriptor(AggregateDeclaration.class, "name", SimpleName.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "templateParameters" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor TEMPLATE_PARAMETERS_PROPERTY =
		new ChildListPropertyDescriptor(AggregateDeclaration.class, "templateParameters", TemplateParameter.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "baseClasses" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor BASE_CLASSES_PROPERTY =
		new ChildListPropertyDescriptor(AggregateDeclaration.class, "baseClasses", BaseClass.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "declarations" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor DECLARATIONS_PROPERTY =
		new ChildListPropertyDescriptor(AggregateDeclaration.class, "declarations", Declaration.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "postDDoc" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POST_D_DOC_PROPERTY =
	internalPostDDocPropertyFactory(AggregateDeclaration.class); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(8);
		createPropertyList(AggregateDeclaration.class, properyList);
		addProperty(PRE_D_DOCS_PROPERTY, properyList);
		addProperty(MODIFIERS_PROPERTY, properyList);
		addProperty(KIND_PROPERTY, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(TEMPLATE_PARAMETERS_PROPERTY, properyList);
		addProperty(BASE_CLASSES_PROPERTY, properyList);
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
	 * The kind.
	 */
	private Kind kind;

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
	 * The base classes
	 * (element type: <code>BaseClass</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList baseClasses =
		new ASTNode.NodeList(BASE_CLASSES_PROPERTY);
	/**
	 * The declarations
	 * (element type: <code>Declaration</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList declarations =
		new ASTNode.NodeList(DECLARATIONS_PROPERTY);

	/**
	 * Creates a new unparented aggregate declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	AggregateDeclaration(AST ast) {
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
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == NAME_PROPERTY) {
			if (get) {
				return getName();
			} else {
				setName((SimpleName) child);
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
		if (property == BASE_CLASSES_PROPERTY) {
			return baseClasses();
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
		return AGGREGATE_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		AggregateDeclaration result = new AggregateDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.preDDocs.addAll(ASTNode.copySubtrees(target, preDDocs()));
		result.modifiers.addAll(ASTNode.copySubtrees(target, modifiers()));
		result.setKind(getKind());
	result.setName((SimpleName) ASTNode.copySubtree(target, getName()));
		result.templateParameters.addAll(ASTNode.copySubtrees(target, templateParameters()));
		result.baseClasses.addAll(ASTNode.copySubtrees(target, baseClasses()));
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
			acceptChildren(visitor, this.templateParameters);
			acceptChildren(visitor, this.baseClasses);
			acceptChildren(visitor, this.declarations);
			acceptChild(visitor, getPostDDoc());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the kind of this aggregate declaration.
	 * 
	 * @return the kind
	 */ 
	public Kind getKind() {
		return this.kind;
	}

	/**
	 * Sets the kind of this aggregate declaration.
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
	 * Returns the name of this aggregate declaration.
	 * 
	 * @return the name
	 */ 
	public SimpleName getName() {
		return this.name;
	}

	/**
	 * Sets the name of this aggregate declaration.
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
	 * Returns the live ordered list of template parameters for this
	 * aggregate declaration.
	 * 
	 * @return the live list of aggregate declaration
	 *    (element type: <code>TemplateParameter</code>)
	 */ 
	public List<TemplateParameter> templateParameters() {
		return this.templateParameters;
	}

	/**
	 * Returns the live ordered list of base classes for this
	 * aggregate declaration.
	 * 
	 * @return the live list of aggregate declaration
	 *    (element type: <code>BaseClass</code>)
	 */ 
	public List<BaseClass> baseClasses() {
		return this.baseClasses;
	}

	/**
	 * Returns the live ordered list of declarations for this
	 * aggregate declaration.
	 * 
	 * @return the live list of aggregate declaration
	 *    (element type: <code>Declaration</code>)
	 */ 
	public List<Declaration> declarations() {
		return this.declarations;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 8 * 4;
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
			+ (this.templateParameters.listSize())
			+ (this.baseClasses.listSize())
			+ (this.declarations.listSize())
			+ (this.postDDoc == null ? 0 : getPostDDoc().treeSize())
	;
	}

}
