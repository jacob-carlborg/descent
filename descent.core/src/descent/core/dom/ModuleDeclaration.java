package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Module declaration AST node.
 * 
 * <pre>
 * ModuleDeclaration:
 *    <b>module</b> Name <b>;</b>
 * </pre>
 */
public class ModuleDeclaration extends ASTNode {
	
	/**
	 * The "modifiers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor MODIFIERS_PROPERTY =
		new ChildListPropertyDescriptor(ModuleDeclaration.class, "modifiers", Modifier.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY =
		new ChildPropertyDescriptor(ModuleDeclaration.class, "name", Name.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "dDocs" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor D_DOCS_PROPERTY =
		new ChildListPropertyDescriptor(ModuleDeclaration.class, "dDocs", Comment.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(3);
		createPropertyList(ModuleDeclaration.class, properyList);
		addProperty(MODIFIERS_PROPERTY, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(D_DOCS_PROPERTY, properyList);
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
	 * The modifiers
	 * (element type: <code>Modifier</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList modifiers =
		new ASTNode.NodeList(MODIFIERS_PROPERTY);
	/**
	 * The name.
	 */
	private Name name;

	/**
	 * The d docs
	 * (element type: <code>Comment</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList dDocs =
		new ASTNode.NodeList(D_DOCS_PROPERTY);

	/**
	 * Creates a new unparented module declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ModuleDeclaration(AST ast) {
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
				setName((Name) child);
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
		if (property == MODIFIERS_PROPERTY) {
			return modifiers();
		}
		if (property == D_DOCS_PROPERTY) {
			return dDocs();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return MODULE_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		ModuleDeclaration result = new ModuleDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.modifiers.addAll(ASTNode.copySubtrees(target, modifiers()));
		result.setName((Name) getName().clone(target));
		result.dDocs.addAll(ASTNode.copySubtrees(target, dDocs()));
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
			acceptChildren(visitor, modifiers);
			acceptChild(visitor, getName());
			acceptChildren(visitor, dDocs);
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the live ordered list of modifiers for this
	 * module declaration.
	 * 
	 * @return the live list of module declaration
	 *    (element type: <code>Modifier</code>)
	 */ 
	public List<Modifier> modifiers() {
		return this.modifiers;
	}

	/**
	 * Returns the name of this module declaration.
	 * 
	 * @return the name
	 */ 
	public Name getName() {
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
	 * Sets the name of this module declaration.
	 * 
	 * @param name the name
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setName(Name name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.name;
		preReplaceChild(oldChild, name, NAME_PROPERTY);
		this.name = name;
		postReplaceChild(oldChild, name, NAME_PROPERTY);
	}

	/**
	 * Returns the live ordered list of d docs for this
	 * module declaration.
	 * 
	 * @return the live list of module declaration
	 *    (element type: <code>Comment</code>)
	 */ 
	public List<Comment> dDocs() {
		return this.dDocs;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 3 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.modifiers.listSize())
			+ (this.name == null ? 0 : getName().treeSize())
			+ (this.dDocs.listSize())
	;
	}
	
}
