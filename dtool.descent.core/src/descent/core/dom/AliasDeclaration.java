package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Alias declaration AST node type.
 *
 * <pre>
 * AliasDeclaration:
 *    { Modifier } <b>alias</b> Type AliasDeclarationFragment { , AliasDeclarationFragment }<b>;</b>
 * </pre>
 */
public class AliasDeclaration extends Declaration {
	
	/**
	 * The "preDDocs" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor PRE_D_DOCS_PROPERTY =
	internalPreDDocsPropertyFactory(AliasDeclaration.class); //$NON-NLS-1$

	/**
	 * The "modifiers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor MODIFIERS_PROPERTY =
	internalModifiersPropertyFactory(AliasDeclaration.class); //$NON-NLS-1$

	/**
	 * The "type" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor TYPE_PROPERTY =
		new ChildPropertyDescriptor(AliasDeclaration.class, "type", Type.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "fragments" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor FRAGMENTS_PROPERTY =
		new ChildListPropertyDescriptor(AliasDeclaration.class, "fragments", AliasDeclarationFragment.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "postDDoc" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POST_D_DOC_PROPERTY =
	internalPostDDocPropertyFactory(AliasDeclaration.class); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(5);
		createPropertyList(AliasDeclaration.class, properyList);
		addProperty(PRE_D_DOCS_PROPERTY, properyList);
		addProperty(MODIFIERS_PROPERTY, properyList);
		addProperty(TYPE_PROPERTY, properyList);
		addProperty(FRAGMENTS_PROPERTY, properyList);
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
	 * The type.
	 */
	private Type type;

	/**
	 * The fragments
	 * (element type: <code>AliasDeclarationFragment</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList fragments =
		new ASTNode.NodeList(FRAGMENTS_PROPERTY);

	/**
	 * Creates a new unparented alias declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	AliasDeclaration(AST ast) {
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
		if (property == TYPE_PROPERTY) {
			if (get) {
				return getType();
			} else {
				setType((Type) child);
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
		if (property == FRAGMENTS_PROPERTY) {
			return fragments();
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
		return ALIAS_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		AliasDeclaration result = new AliasDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.preDDocs.addAll(ASTNode.copySubtrees(target, preDDocs()));
		result.modifiers.addAll(ASTNode.copySubtrees(target, modifiers()));
		result.setType((Type) getType().clone(target));
		result.fragments.addAll(ASTNode.copySubtrees(target, fragments()));
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
			acceptChild(visitor, getType());
			acceptChildren(visitor, this.fragments);
			acceptChild(visitor, getPostDDoc());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the type of this alias declaration.
	 * 
	 * @return the type
	 */ 
	public Type getType() {
		if (this.type == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.type == null) {
					preLazyInit();
					this.type = new PrimitiveType(this.ast);
					postLazyInit(this.type, TYPE_PROPERTY);
				}
			}
		}
		return this.type;
	}

	/**
	 * Sets the type of this alias declaration.
	 * 
	 * @param type the type
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
	 * Returns the live ordered list of fragments for this
	 * alias declaration.
	 * 
	 * @return the live list of alias declaration
	 *    (element type: <code>AliasDeclarationFragment</code>)
	 */ 
	public List<AliasDeclarationFragment> fragments() {
		return this.fragments;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 5 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.preDDocs.listSize())
			+ (this.modifiers.listSize())
			+ (this.type == null ? 0 : getType().treeSize())
			+ (this.fragments.listSize())
			+ (this.postDDoc == null ? 0 : getPostDDoc().treeSize())
	;
	}

}
