package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Import AST node.
 * 
 * <pre>
 * Import:
 *    [ SimpleName <b>=</b> ] Name [ <b>:</b> SelectiveImport { <b>,</b> SelectiveImport } ]
 * </pre>
 */
public class Import extends ASTNode {
	
	/**
	 * The "alias" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor ALIAS_PROPERTY =
		new ChildPropertyDescriptor(Import.class, "alias", SimpleName.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY =
		new ChildPropertyDescriptor(Import.class, "name", Name.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "selectiveImports" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor SELECTIVE_IMPORTS_PROPERTY =
		new ChildListPropertyDescriptor(Import.class, "selectiveImports", SelectiveImport.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(3);
		createPropertyList(Import.class, properyList);
		addProperty(ALIAS_PROPERTY, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(SELECTIVE_IMPORTS_PROPERTY, properyList);
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
	 * The alias.
	 */
	private SimpleName alias;

	/**
	 * The name.
	 */
	private Name name;

	/**
	 * The selectiveImports
	 * (element type: <code>SelectiveImport</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList selectiveImports =
		new ASTNode.NodeList(SELECTIVE_IMPORTS_PROPERTY);

	/**
	 * Creates a new unparented import node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	Import(AST ast) {
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
		if (property == ALIAS_PROPERTY) {
			if (get) {
				return getAlias();
			} else {
				setAlias((SimpleName) child);
				return null;
			}
		}
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
		if (property == SELECTIVE_IMPORTS_PROPERTY) {
			return selectiveImports();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return IMPORT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		Import result = new Import(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
	result.setAlias((SimpleName) ASTNode.copySubtree(target, getAlias()));
		result.setName((Name) getName().clone(target));
		result.selectiveImports.addAll(ASTNode.copySubtrees(target, selectiveImports()));
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
			acceptChild(visitor, getAlias());
			acceptChild(visitor, getName());
			acceptChildren(visitor, selectiveImports);
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the alias of this import.
	 * 
	 * @return the alias
	 */ 
	public SimpleName getAlias() {
		return this.alias;
	}

	/**
	 * Sets the alias of this import.
	 * 
	 * @param alias the alias
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setAlias(SimpleName alias) {
		ASTNode oldChild = this.alias;
		preReplaceChild(oldChild, alias, ALIAS_PROPERTY);
		this.alias = alias;
		postReplaceChild(oldChild, alias, ALIAS_PROPERTY);
	}

	/**
	 * Returns the name of this import.
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
	 * Sets the name of this import.
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
	 * Returns the live ordered list of selectiveImports for this
	 * import.
	 * 
	 * @return the live list of import
	 *    (element type: <code>SelectiveImport</code>)
	 */ 
	public List<SelectiveImport> selectiveImports() {
		return this.selectiveImports;
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
			+ (this.alias == null ? 0 : getAlias().treeSize())
			+ (this.name == null ? 0 : getName().treeSize())
			+ (this.selectiveImports.listSize())
	;
	}
	
	/**
	 * Resolves and returns the binding for this import.
	 * <p>
	 * Note that bindings are generally unavailable unless requested when the
	 * AST is being built.
	 * </p>
	 * 
	 * @return the binding, or <code>null</code> if the binding cannot be 
	 *    resolved
	 */	
	public final IBinding resolveBinding() {
		/* TODO binding
		return this.ast.getBindingResolver().resolveImport(this);
		*/
		return null;
	}

}
