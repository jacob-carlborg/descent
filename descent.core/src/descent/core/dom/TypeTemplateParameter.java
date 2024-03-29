package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Type template parameter AST node type.
 *
 * <pre>
 * TypeTemplateParameter:
 *    SimpleName [ <b>:</b> Type ] [ <b>=</b> Type ]
 * </pre>
 */
public class TypeTemplateParameter extends TemplateParameter {

	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY =
	internalNamePropertyFactory(TypeTemplateParameter.class); //$NON-NLS-1$

	/**
	 * The "specificType" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor SPECIFIC_TYPE_PROPERTY =
		new ChildPropertyDescriptor(TypeTemplateParameter.class, "specificType", Type.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "defaultType" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor DEFAULT_TYPE_PROPERTY =
		new ChildPropertyDescriptor(TypeTemplateParameter.class, "defaultType", Type.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(3);
		createPropertyList(TypeTemplateParameter.class, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(SPECIFIC_TYPE_PROPERTY, properyList);
		addProperty(DEFAULT_TYPE_PROPERTY, properyList);
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
	 * The specificType.
	 */
	private Type specificType;

	/**
	 * The defaultType.
	 */
	private Type defaultType;


	/**
	 * Creates a new unparented type template parameter node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	TypeTemplateParameter(AST ast) {
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
		if (property == SPECIFIC_TYPE_PROPERTY) {
			if (get) {
				return getSpecificType();
			} else {
				setSpecificType((Type) child);
				return null;
			}
		}
		if (property == DEFAULT_TYPE_PROPERTY) {
			if (get) {
				return getDefaultType();
			} else {
				setDefaultType((Type) child);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetChildProperty(property, get, child);
	}

		@Override
		final ChildPropertyDescriptor internalNameProperty() {
			return NAME_PROPERTY;
		}
		
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int getNodeType0() {
		return TYPE_TEMPLATE_PARAMETER;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		TypeTemplateParameter result = new TypeTemplateParameter(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setName((SimpleName) getName().clone(target));
	result.setSpecificType((Type) ASTNode.copySubtree(target, getSpecificType()));
	result.setDefaultType((Type) ASTNode.copySubtree(target, getDefaultType()));
		return result;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	boolean subtreeMatch0(ASTMatcher matcher, Object other) {
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
			acceptChild(visitor, getName());
			acceptChild(visitor, getSpecificType());
			acceptChild(visitor, getDefaultType());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the specific type of this type template parameter.
	 * 
	 * @return the specific type
	 */ 
	public Type getSpecificType() {
		return this.specificType;
	}

	/**
	 * Sets the specific type of this type template parameter.
	 * 
	 * @param specificType the specific type
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setSpecificType(Type specificType) {
		ASTNode oldChild = this.specificType;
		preReplaceChild(oldChild, specificType, SPECIFIC_TYPE_PROPERTY);
		this.specificType = specificType;
		postReplaceChild(oldChild, specificType, SPECIFIC_TYPE_PROPERTY);
	}

	/**
	 * Returns the default type of this type template parameter.
	 * 
	 * @return the default type
	 */ 
	public Type getDefaultType() {
		return this.defaultType;
	}

	/**
	 * Sets the default type of this type template parameter.
	 * 
	 * @param defaultType the default type
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setDefaultType(Type defaultType) {
		ASTNode oldChild = this.defaultType;
		preReplaceChild(oldChild, defaultType, DEFAULT_TYPE_PROPERTY);
		this.defaultType = defaultType;
		postReplaceChild(oldChild, defaultType, DEFAULT_TYPE_PROPERTY);
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
			+ (this.name == null ? 0 : getName().treeSize())
			+ (this.specificType == null ? 0 : getSpecificType().treeSize())
			+ (this.defaultType == null ? 0 : getDefaultType().treeSize())
	;
	}

}
