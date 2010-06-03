package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Post blit declaration AST node.
 * 
 * <pre>
 * PostblitDeclaration:
 *     ( <b>this(this)</b> Block | 
 *       <b>=this()</b> Block )
 * </pre>
 */
public class PostblitDeclaration extends Declaration {
	
	/**
	 * The "preDDocs" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor PRE_D_DOCS_PROPERTY =
	internalPreDDocsPropertyFactory(PostblitDeclaration.class); //$NON-NLS-1$

	/**
	 * The "modifiers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor MODIFIERS_PROPERTY =
	internalModifiersPropertyFactory(PostblitDeclaration.class); //$NON-NLS-1$
	
	/**
	 * The "body" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor BODY_PROPERTY =
		new ChildPropertyDescriptor(PostblitDeclaration.class, "body", Block.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * The "precondition" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor PRECONDITION_PROPERTY =
		new ChildPropertyDescriptor(PostblitDeclaration.class, "precondition", Block.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * The "postcondition" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POSTCONDITION_PROPERTY =
		new ChildPropertyDescriptor(PostblitDeclaration.class, "postcondition", Block.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * The "postconditionVariableName" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POSTCONDITION_VARIABLE_NAME_PROPERTY =
		new ChildPropertyDescriptor(PostblitDeclaration.class, "postconditionVariableName", SimpleName.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * The "postDDoc" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POST_D_DOC_PROPERTY =
		internalPostDDocPropertyFactory(PostblitDeclaration.class); //$NON-NLS-1$
	
	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(7);
		createPropertyList(PostblitDeclaration.class, properyList);
		addProperty(PRE_D_DOCS_PROPERTY, properyList);
		addProperty(MODIFIERS_PROPERTY, properyList);
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
	 * The precondition.
	 */
	private Block precondition;
	
	/**
	 * The postcondition.
	 */
	private Block postcondition;
	
	/**
	 * The postcondition variable name.
	 */
	private SimpleName postconditionVariableName;
	
	/**
	 * The body.
	 */
	private Block body;
	
	/**
	 * Creates a new unparented post blit declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	PostblitDeclaration(AST ast) {
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
		return POSTBLIT_DECLARATION;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		PostblitDeclaration result = new PostblitDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.preDDocs.addAll(ASTNode.copySubtrees(target, preDDocs()));
		result.modifiers.addAll(ASTNode.copySubtrees(target, modifiers()));
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
			acceptChildren(visitor, this.preDDocs);
			acceptChildren(visitor, this.modifiers);
			acceptChild(visitor, getPrecondition());
			acceptChild(visitor, getPostcondition());
			acceptChild(visitor, getPostconditionVariableName());
			acceptChild(visitor, getBody());
			acceptChild(visitor, getPostDDoc());
		}
		visitor.endVisit(this);
	}
	
	/**
	 * Returns the body of this postblit declaration.
	 * 
	 * @return the body
	 */ 
	public Block getBody() {
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
	 * Sets the precondition of this postblit declaration.
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
		preReplaceChild(oldChild, precondition, PRECONDITION_PROPERTY);
		this.precondition = precondition;
		postReplaceChild(oldChild, precondition, PRECONDITION_PROPERTY);
	}
	
	/**
	 * Returns the precondition of this postblit declaration.
	 * 
	 * @return the precondition
	 */ 
	public Block getPrecondition() {
		return this.precondition;
	}
	
	/**
	 * Sets the postcondition of this postblit declaration.
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
		preReplaceChild(oldChild, postcondition, POSTCONDITION_PROPERTY);
		this.postcondition = postcondition;
		postReplaceChild(oldChild, postcondition, POSTCONDITION_PROPERTY);
	}
	
	/**
	 * Returns the postcondition of this postblit declaration.
	 * 
	 * @return the postcondition
	 */ 
	public Block getPostcondition() {
		return this.postcondition;
	}
	
	/**
	 * Sets the postcondition variable name of this postblit declaration.
	 * 
	 * @param name the postcondition variable name
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setPostconditionVariableName(SimpleName name) {
		ASTNode oldChild = this.postconditionVariableName;
		preReplaceChild(oldChild, name, POSTCONDITION_VARIABLE_NAME_PROPERTY);
		this.postconditionVariableName = name;
		postReplaceChild(oldChild, name, POSTCONDITION_VARIABLE_NAME_PROPERTY);
	}
	
	/**
	 * Returns the postcondition variable name of this postblit declaration.
	 * 
	 * @return the postcondition variable name 
	 */ 
	public SimpleName getPostconditionVariableName() {
		return this.postconditionVariableName;
	}

	/**
	 * Sets the body of this postblit declaration.
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
		return BASE_NODE_SIZE + 4 * 4;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.preDDocs.listSize())
			+ (this.modifiers.listSize())
			+ (this.precondition == null ? 0 : getPrecondition().treeSize())
			+ (this.postcondition == null ? 0 : getPostcondition().treeSize())
			+ (this.postconditionVariableName == null ? 0 : getPostconditionVariableName().treeSize())
			+ (this.body == null ? 0 : getBody().treeSize())
			+ (this.postDDoc == null ? 0 : getPostDDoc().treeSize())
	;
	}

}
