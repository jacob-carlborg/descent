package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IAliasDeclaration;

/**
 * Alias declaration AST node type.
 *
 * <pre>
 * AliasDeclaration:
 *    <b>alias</b> Type AliasDeclarationFragment { , AliasDeclarationFragment }<b>;</b>
 * </pre>
 */
public class AliasDeclaration extends Declaration implements IAliasDeclaration {
	
	/**
	 * The "modifierFlags" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor MODIFIER_FLAGS_PROPERTY =
		new SimplePropertyDescriptor(AliasDeclaration.class, "modifierFlags", int.class, OPTIONAL); //$NON-NLS-1$

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
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(3);
		createPropertyList(AliasDeclaration.class, properyList);
		addProperty(MODIFIER_FLAGS_PROPERTY, properyList);
		addProperty(TYPE_PROPERTY, properyList);
		addProperty(FRAGMENTS_PROPERTY, properyList);
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
	 * The modifierFlags.
	 */
	//private int modifierFlags;

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
	final int internalGetSetIntProperty(SimplePropertyDescriptor property, boolean get, int value) {
		if (property == MODIFIER_FLAGS_PROPERTY) {
			if (get) {
				return getModifierFlags();
			} else {
				setModifierFlags(value);
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
		if (property == TYPE_PROPERTY) {
			if (get) {
				return getType();
			} else {
				setType((Type) child);
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
		if (property == FRAGMENTS_PROPERTY) {
			return fragments();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return ALIAS_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		AliasDeclaration result = new AliasDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setModifierFlags(getModifierFlags());
		result.setType((Type) getType().clone(target));
		result.fragments.addAll(ASTNode.copySubtrees(target, fragments()));
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
			acceptChild(visitor, getType());
			acceptChildren(visitor, fragments());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the modifier flags of this alias declaration.
	 * 
	 * @return the modifier flags
	 */ 
	public int getModifierFlags() {
		return this.modifierFlags;
	}

	/**
	 * Sets the modifier flags of this alias declaration.
	 * 
	 * @param modifierFlags the modifier flags
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setModifierFlags(int modifierFlags) {
		preValueChange(MODIFIER_FLAGS_PROPERTY);
		this.modifierFlags = modifierFlags;
		postValueChange(MODIFIER_FLAGS_PROPERTY);
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
		return BASE_NODE_SIZE + 3 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.type == null ? 0 : getType().treeSize())
			+ (this.fragments.listSize())
	;
	}

}
