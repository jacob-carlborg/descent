package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Enum member AST node type.
 *
 * <pre>
 * EnumMember:
 *    SimpleName [ <b>=</b> Expression ]
 * </pre>
 */
public class EnumMember extends ASTNode {
	
	/**
	 * The "preDDocs" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor PRE_D_DOCS_PROPERTY =
		new ChildListPropertyDescriptor(EnumMember.class, "preDDocs", CodeComment.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY =
		new ChildPropertyDescriptor(EnumMember.class, "name", SimpleName.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "value" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor VALUE_PROPERTY =
		new ChildPropertyDescriptor(EnumMember.class, "value", Expression.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "postDDoc" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POST_D_DOC_PROPERTY =
		new ChildPropertyDescriptor(EnumMember.class, "postDDoc", CodeComment.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(4);
		createPropertyList(EnumMember.class, properyList);
		addProperty(PRE_D_DOCS_PROPERTY, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(VALUE_PROPERTY, properyList);
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
	 * The pre d docs
	 * (element type: <code>Comment</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList preDDocs =
		new ASTNode.NodeList(PRE_D_DOCS_PROPERTY);
	/**
	 * The name.
	 */
	private SimpleName name;

	/**
	 * The value.
	 */
	private Expression value;

	/**
	 * The postDDoc.
	 */
	private CodeComment postDDoc;


	/**
	 * Creates a new unparented enum member node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	EnumMember(AST ast) {
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
		if (property == VALUE_PROPERTY) {
			if (get) {
				return getValue();
			} else {
				setValue((Expression) child);
				return null;
			}
		}
		if (property == POST_D_DOC_PROPERTY) {
			if (get) {
				return getPostDDoc();
			} else {
				setPostDDoc((CodeComment) child);
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
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return ENUM_MEMBER;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		EnumMember result = new EnumMember(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.preDDocs.addAll(ASTNode.copySubtrees(target, preDDocs()));
		result.setName((SimpleName) getName().clone(target));
	result.setValue((Expression) ASTNode.copySubtree(target, getValue()));
	result.setPostDDoc((CodeComment) ASTNode.copySubtree(target, getPostDDoc()));
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
			acceptChild(visitor, getName());
			acceptChild(visitor, getValue());
			acceptChild(visitor, getPostDDoc());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the live ordered list of pre d docs for this
	 * enum member.
	 * 
	 * @return the live list of enum member
	 *    (element type: <code>Comment</code>)
	 */ 
	public List<CodeComment> preDDocs() {
		return this.preDDocs;
	}

	/**
	 * Returns the name of this enum member.
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
	 * Sets the name of this enum member.
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
	 * Returns the value of this enum member.
	 * 
	 * @return the value
	 */ 
	public Expression getValue() {
		return this.value;
	}

	/**
	 * Sets the value of this enum member.
	 * 
	 * @param value the value
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setValue(Expression value) {
		ASTNode oldChild = this.value;
		preReplaceChild(oldChild, value, VALUE_PROPERTY);
		this.value = value;
		postReplaceChild(oldChild, value, VALUE_PROPERTY);
	}

	/**
	 * Returns the post d doc of this enum member.
	 * 
	 * @return the post d doc
	 */ 
	public CodeComment getPostDDoc() {
		return this.postDDoc;
	}

	/**
	 * Sets the post d doc of this enum member.
	 * 
	 * @param postDDoc the post d doc
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setPostDDoc(CodeComment postDDoc) {
		ASTNode oldChild = this.postDDoc;
		preReplaceChild(oldChild, postDDoc, POST_D_DOC_PROPERTY);
		this.postDDoc = postDDoc;
		postReplaceChild(oldChild, postDDoc, POST_D_DOC_PROPERTY);
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
			+ (this.name == null ? 0 : getName().treeSize())
			+ (this.value == null ? 0 : getValue().treeSize())
			+ (this.postDDoc == null ? 0 : getPostDDoc().treeSize())
	;
	}
	
}
