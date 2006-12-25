package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Enum declaration AST node.
 * 
 * <pre>
 * EnumDeclaration:
 *    { Modifier } <b>enum</b> SimpleName [ <b>:</b> Type ]
 *    <b>{</b>
 *       { Declaration }
 *    <b>}</b>
 * </pre>
 */
public class EnumDeclaration extends Declaration {
	
	/**
	 * The "modifiers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor MODIFIERS_PROPERTY =
		internalModifiersPropertyFactory(EnumDeclaration.class); //$NON-NLS-1$

	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY =
		new ChildPropertyDescriptor(EnumDeclaration.class, "name", SimpleName.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "baseType" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor BASETYPE_PROPERTY =
		new ChildPropertyDescriptor(EnumDeclaration.class, "baseType", Type.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "enumMembers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor ENUMMEMBERS_PROPERTY =
		new ChildListPropertyDescriptor(EnumDeclaration.class, "enumMembers", EnumMember.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(4);
		createPropertyList(EnumDeclaration.class, properyList);
		addProperty(MODIFIERS_PROPERTY, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(BASETYPE_PROPERTY, properyList);
		addProperty(ENUMMEMBERS_PROPERTY, properyList);
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
	private SimpleName name;

	/**
	 * The baseType.
	 */
	private Type baseType;

	/**
	 * The enumMembers
	 * (element type: <code>EnumMember</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList enumMembers =
		new ASTNode.NodeList(ENUMMEMBERS_PROPERTY);

	/**
	 * Creates a new unparented enum declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	EnumDeclaration(AST ast) {
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
		if (property == BASETYPE_PROPERTY) {
			if (get) {
				return getBaseType();
			} else {
				setBaseType((Type) child);
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
		if (property == ENUMMEMBERS_PROPERTY) {
			return enumMembers();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

		@Override
		final ChildListPropertyDescriptor internalModifiersProperty() {
			return MODIFIERS_PROPERTY;
		}
		
		/* (omit javadoc for this method)
		 * Method declared on ASTNode.
		 */
		final int getNodeType0() {
		return ENUM_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		EnumDeclaration result = new EnumDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.modifiers.addAll(ASTNode.copySubtrees(target, modifiers()));
	result.setName((SimpleName) ASTNode.copySubtree(target, getName()));
	result.setBaseType((Type) ASTNode.copySubtree(target, getBaseType()));
		result.enumMembers.addAll(ASTNode.copySubtrees(target, enumMembers()));
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
			acceptChildren(visitor, modifiers());
			acceptChild(visitor, getName());
			acceptChild(visitor, getBaseType());
			acceptChildren(visitor, enumMembers());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the name of this enum declaration.
	 * 
	 * @return the name
	 */ 
	public SimpleName getName() {
		return this.name;
	}

	/**
	 * Sets the name of this enum declaration.
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
	 * Returns the baseType of this enum declaration.
	 * 
	 * @return the baseType
	 */ 
	public Type getBaseType() {
		return this.baseType;
	}

	/**
	 * Sets the baseType of this enum declaration.
	 * 
	 * @param baseType the baseType
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setBaseType(Type baseType) {
		ASTNode oldChild = this.baseType;
		preReplaceChild(oldChild, baseType, BASETYPE_PROPERTY);
		this.baseType = baseType;
		postReplaceChild(oldChild, baseType, BASETYPE_PROPERTY);
	}

	/**
	 * Returns the live ordered list of enumMembers for this
	 * enum declaration.
	 * 
	 * @return the live list of enum declaration
	 *    (element type: <code>EnumMember</code>)
	 */ 
	public List<EnumMember> enumMembers() {
		return this.enumMembers;
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
			+ (this.modifiers.listSize())
			+ (this.name == null ? 0 : getName().treeSize())
			+ (this.baseType == null ? 0 : getBaseType().treeSize())
			+ (this.enumMembers.listSize())
	;
	}

}
