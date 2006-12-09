package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.ITemplateDeclaration;

/**
 * Template declaration AST node type.
 *
 * <pre>
 * TemplateDeclaration:
 *    <b>template</b> SimpleName <b>( [ TemplateParameter { <b>,</b> TemplateParameter } ] 
 *    <b>{</b> 
 *       { Declaration } 
 *    <b>}</b>
 * </pre>
 */
public class TemplateDeclaration extends Declaration implements ITemplateDeclaration {
	
	/**
	 * The "modifierFlags" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor MODIFIER_FLAGS_PROPERTY =
		new SimplePropertyDescriptor(TemplateDeclaration.class, "modifierFlags", int.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY =
		new ChildPropertyDescriptor(TemplateDeclaration.class, "name", SimpleName.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "templateParameters" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor TEMPLATEPARAMETERS_PROPERTY =
		new ChildListPropertyDescriptor(TemplateDeclaration.class, "templateParameters", TemplateParameter.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "declarations" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor DECLARATIONS_PROPERTY =
		new ChildListPropertyDescriptor(TemplateDeclaration.class, "declarations", Declaration.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(4);
		createPropertyList(TemplateDeclaration.class, properyList);
		addProperty(MODIFIER_FLAGS_PROPERTY, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(TEMPLATEPARAMETERS_PROPERTY, properyList);
		addProperty(DECLARATIONS_PROPERTY, properyList);
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
	 * TODO uncomment
	 */
	// private int modifierFlags;

	/**
	 * The name.
	 */
	private SimpleName name;

	/**
	 * The templateParameters
	 * (element type: <code>TemplateParameter</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList templateParameters =
		new ASTNode.NodeList(TEMPLATEPARAMETERS_PROPERTY);
	/**
	 * The declarations
	 * (element type: <code>Declaration</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList declarations =
		new ASTNode.NodeList(DECLARATIONS_PROPERTY);

	/**
	 * Creates a new unparented template declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	TemplateDeclaration(AST ast) {
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
		if (property == NAME_PROPERTY) {
			if (get) {
				return getName();
			} else {
				setName((SimpleName) child);
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
		if (property == TEMPLATEPARAMETERS_PROPERTY) {
			return templateParameters();
		}
		if (property == DECLARATIONS_PROPERTY) {
			return declarations();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return TEMPLATE_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		TemplateDeclaration result = new TemplateDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setModifierFlags(getModifierFlags());
		result.setName((SimpleName) getName().clone(target));
		result.templateParameters.addAll(ASTNode.copySubtrees(target, templateParameters()));
		result.declarations.addAll(ASTNode.copySubtrees(target, declarations()));
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
			acceptChildren(visitor, templateParameters());
			acceptChildren(visitor, declarations());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the modifier flags of this template declaration.
	 * 
	 * @return the modifier flags
	 */ 
	public int getModifierFlags() {
		return this.modifierFlags;
	}

	/**
	 * Sets the modifier flags of this template declaration.
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
	 * Returns the name of this template declaration.
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
	 * Sets the name of this template declaration.
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
	 * Returns the live ordered list of templateParameters for this
	 * template declaration.
	 * 
	 * @return the live list of template declaration
	 *    (element type: <code>TemplateParameter</code>)
	 */ 
	public List<TemplateParameter> templateParameters() {
		return this.templateParameters;
	}

	/**
	 * Returns the live ordered list of declarations for this
	 * template declaration.
	 * 
	 * @return the live list of template declaration
	 *    (element type: <code>Declaration</code>)
	 */ 
	public List<Declaration> declarations() {
		return this.declarations;
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
			+ (this.name == null ? 0 : getName().treeSize())
			+ (this.templateParameters.listSize())
			+ (this.declarations.listSize())
	;
	}

}
