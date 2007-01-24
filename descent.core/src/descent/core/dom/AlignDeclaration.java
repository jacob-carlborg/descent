package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Align declaration AST node type.
 *
 * <pre>
 * AliasDeclaration:
 *    { Modifier } <b>align</b> <b>(</b> Integer <b>)</b> { Declaration }
 * </pre>
 */
public class AlignDeclaration extends Declaration {
	
	/**
	 * The "preDDocs" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor PRE_D_DOCS_PROPERTY =
	internalPreDDocsPropertyFactory(AlignDeclaration.class); //$NON-NLS-1$

	/**
	 * The "modifiers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor MODIFIERS_PROPERTY =
	internalModifiersPropertyFactory(AlignDeclaration.class); //$NON-NLS-1$

	/**
	 * The "align" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor ALIGN_PROPERTY =
		new SimplePropertyDescriptor(AlignDeclaration.class, "align", int.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "declarations" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor DECLARATIONS_PROPERTY =
		new ChildListPropertyDescriptor(AlignDeclaration.class, "declarations", Declaration.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "postDDoc" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POST_D_DOC_PROPERTY =
	internalPostDDocPropertyFactory(AlignDeclaration.class); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(5);
		createPropertyList(AlignDeclaration.class, properyList);
		addProperty(PRE_D_DOCS_PROPERTY, properyList);
		addProperty(MODIFIERS_PROPERTY, properyList);
		addProperty(ALIGN_PROPERTY, properyList);
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
	 * The align.
	 */
	private int align;

	/**
	 * The declarations
	 * (element type: <code>Declaration</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList declarations =
		new ASTNode.NodeList(DECLARATIONS_PROPERTY);

	/**
	 * Creates a new unparented align declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	AlignDeclaration(AST ast) {
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
	final int internalGetSetIntProperty(SimplePropertyDescriptor property, boolean get, int value) {
		if (property == ALIGN_PROPERTY) {
			if (get) {
				return getAlign();
			} else {
				setAlign(value);
				return 0;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetIntProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == POST_D_DOC_PROPERTY) {
			if (get) {
				return getPostDDoc();
			} else {
				setPostDDoc((Comment) child);
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
		return ALIGN_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		AlignDeclaration result = new AlignDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.preDDocs.addAll(ASTNode.copySubtrees(target, preDDocs()));
		result.modifiers.addAll(ASTNode.copySubtrees(target, modifiers()));
		result.setAlign(getAlign());
		result.declarations.addAll(ASTNode.copySubtrees(target, declarations()));
	result.setPostDDoc((Comment) ASTNode.copySubtree(target, getPostDDoc()));
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
			acceptChildren(visitor, this.declarations);
			acceptChild(visitor, getPostDDoc());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the align of this align declaration.
	 * 
	 * @return the align
	 */ 
	public int getAlign() {
		return this.align;
	}

	/**
	 * Sets the align of this align declaration.
	 * 
	 * @param align the align
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setAlign(int align) {
		preValueChange(ALIGN_PROPERTY);
		this.align = align;
		postValueChange(ALIGN_PROPERTY);
	}

	/**
	 * Returns the live ordered list of declarations for this
	 * align declaration.
	 * 
	 * @return the live list of align declaration
	 *    (element type: <code>Declaration</code>)
	 */ 
	public List<Declaration> declarations() {
		return this.declarations;
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
			+ (this.declarations.listSize())
			+ (this.postDDoc == null ? 0 : getPostDDoc().treeSize())
	;
	}

}
