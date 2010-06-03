package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Alias template parameter AST node type.
 *
 * <pre>
 * AliasTemplateParameter:
 *    <b>alias</b> SimpleName [ <b>:</b> ASTNode ] [ <b>=</b> ASTNode ]
 * </pre>
 */
public class AliasTemplateParameter extends TemplateParameter {
	
	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY =
	internalNamePropertyFactory(AliasTemplateParameter.class); //$NON-NLS-1$

	/**
	 * The "specificNode" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor SPECIFIC_NODE_PROPERTY =
		new ChildPropertyDescriptor(AliasTemplateParameter.class, "specificType", ASTNode.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "defaultNode" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor DEFAULT_NODE_PROPERTY =
		new ChildPropertyDescriptor(AliasTemplateParameter.class, "defaultType", ASTNode.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(3);
		createPropertyList(AliasTemplateParameter.class, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(SPECIFIC_NODE_PROPERTY, properyList);
		addProperty(DEFAULT_NODE_PROPERTY, properyList);
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
	 * The specificObject.
	 */
	private ASTNode specificObject;

	/**
	 * The defaultObject.
	 */
	private ASTNode defaultObject;


	/**
	 * Creates a new unparented alias template parameter node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	AliasTemplateParameter(AST ast) {
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
		if (property == SPECIFIC_NODE_PROPERTY) {
			if (get) {
				return getSpecificObject();
			} else {
				setSpecificObject((Type) child);
				return null;
			}
		}
		if (property == DEFAULT_NODE_PROPERTY) {
			if (get) {
				return getDefaultObject();
			} else {
				setDefaultObject((Type) child);
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
	final int getNodeType0() {
		return ALIAS_TEMPLATE_PARAMETER;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		AliasTemplateParameter result = new AliasTemplateParameter(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setName((SimpleName) getName().clone(target));
	result.setSpecificObject((Type) ASTNode.copySubtree(target, getSpecificObject()));
	result.setDefaultObject((Type) ASTNode.copySubtree(target, getDefaultObject()));
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
			acceptChild(visitor, getName());
			acceptChild(visitor, getSpecificObject());
			acceptChild(visitor, getDefaultObject());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the specific object of this alias template parameter.
	 * 
	 * @return the specific object
	 */ 
	public ASTNode getSpecificObject() {
		return this.specificObject;
	}

	/**
	 * Sets the specific object of this alias template parameter.
	 * 
	 * @param specificObject the specific object
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setSpecificObject(ASTNode specificObject) {
		ASTNode oldChild = this.specificObject;
		preReplaceChild(oldChild, specificObject, SPECIFIC_NODE_PROPERTY);
		this.specificObject = specificObject;
		postReplaceChild(oldChild, specificObject, SPECIFIC_NODE_PROPERTY);
	}

	/**
	 * Returns the default object of this alias template parameter.
	 * 
	 * @return the default object
	 */ 
	public ASTNode getDefaultObject() {
		return this.defaultObject;
	}

	/**
	 * Sets the default object of this alias template parameter.
	 * 
	 * @param defaultObject the default type
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setDefaultObject(ASTNode defaultObject) {
		ASTNode oldChild = this.defaultObject;
		preReplaceChild(oldChild, defaultObject, DEFAULT_NODE_PROPERTY);
		this.defaultObject = defaultObject;
		postReplaceChild(oldChild, defaultObject, DEFAULT_NODE_PROPERTY);
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
			+ (this.specificObject == null ? 0 : getSpecificObject().treeSize())
			+ (this.defaultObject == null ? 0 : getDefaultObject().treeSize())
	;
	}

}
