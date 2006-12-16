package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IModifierDeclaration;

/**
 * <p>A protection declaration AST node.</p>
 * 
 * <pre>
 * ProtectionDeclaration:
 *    [ Modifier <b>:</b> { Declaration } | Modifier <b>{</b> { Declaration } <b>}</b> ] 
 * </pre>
 * 
 * <p>Note that if a modifier keyword is not followed by <b>;</b> or by <b>{</b> then
 * the modifier goes to the modifiers of the following declaration.</p>
 * 
 * TODO: comment better
 */
public class ModifierDeclaration extends Declaration implements IModifierDeclaration {
	
	/**
	 * The syntax used in the declaration.
	 */
	public static enum Syntax {
		/** 
		 * The syntax is:
		 * 
		 * <pre>
		 * ProtectionDeclaration:
		 *    Modifier <b>{</b> { Declaration } <b>}</b> 
		 * </pre>
		 */ 
		CURLY_BRACES,
		/** 
		 * The syntax is:
		 * 
		 * <pre>
		 * ProtectionDeclaration:
		 *    Modifier <b>:</b> { Declaration } 
		 * </pre>
		 */
		COLON
	}
	
	/**
	 * The "syntax" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor SYNTAX_PROPERTY =
		new SimplePropertyDescriptor(ModifierDeclaration.class, "syntax", Syntax.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "modifier" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor MODIFIER_PROPERTY =
		new ChildPropertyDescriptor(ModifierDeclaration.class, "modifier", Modifier.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "declarations" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor DECLARATIONS_PROPERTY =
		new ChildListPropertyDescriptor(ModifierDeclaration.class, "declarations", Declaration.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(3);
		createPropertyList(ModifierDeclaration.class, properyList);
		addProperty(SYNTAX_PROPERTY, properyList);
		addProperty(MODIFIER_PROPERTY, properyList);
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
	 * The syntax.
	 */
	private Syntax syntax;

	/**
	 * The modifier.
	 */
	private Modifier modifier;

	/**
	 * The declarations
	 * (element type: <code>Declaration</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList declarations =
		new ASTNode.NodeList(DECLARATIONS_PROPERTY);

	/**
	 * Creates a new unparented modifier declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ModifierDeclaration(AST ast) {
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
	final Object internalGetSetObjectProperty(SimplePropertyDescriptor property, boolean get, Object value) {
		if (property == SYNTAX_PROPERTY) {
			if (get) {
				return getSyntax();
			} else {
				setSyntax((Syntax) value);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetObjectProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == MODIFIER_PROPERTY) {
			if (get) {
				return getModifier();
			} else {
				setModifier((Modifier) child);
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
		return MODIFIER_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		ModifierDeclaration result = new ModifierDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setSyntax(getSyntax());
		result.setModifier((Modifier) getModifier().clone(target));
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
			acceptChild(visitor, getModifier());
			acceptChildren(visitor, declarations());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the syntax of this modifier declaration.
	 * 
	 * @return the syntax
	 */ 
	public Syntax getSyntax() {
		return this.syntax;
	}

	/**
	 * Sets the syntax of this modifier declaration.
	 * 
	 * @param syntax the syntax
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setSyntax(Syntax syntax) {
		if (syntax == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(SYNTAX_PROPERTY);
		this.syntax = syntax;
		postValueChange(SYNTAX_PROPERTY);
	}

	/**
	 * Returns the modifier of this modifier declaration.
	 * 
	 * @return the modifier
	 */ 
	public Modifier getModifier() {
		if (this.modifier == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.modifier == null) {
					preLazyInit();
					this.modifier = new Modifier(this.ast);
					postLazyInit(this.modifier, MODIFIER_PROPERTY);
				}
			}
		}
		return this.modifier;
	}

	/**
	 * Sets the modifier of this modifier declaration.
	 * 
	 * @param modifier the modifier
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setModifier(Modifier modifier) {
		if (modifier == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.modifier;
		preReplaceChild(oldChild, modifier, MODIFIER_PROPERTY);
		this.modifier = modifier;
		postReplaceChild(oldChild, modifier, MODIFIER_PROPERTY);
	}

	/**
	 * Returns the live ordered list of declarations for this
	 * modifier declaration.
	 * 
	 * @return the live list of modifier declaration
	 *    (element type: <code>Declaration</code>)
	 */ 
	public List<Declaration> declarations() {
		return this.declarations;
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
			+ (this.modifier == null ? 0 : getModifier().treeSize())
			+ (this.declarations.listSize())
	;
	}

}
