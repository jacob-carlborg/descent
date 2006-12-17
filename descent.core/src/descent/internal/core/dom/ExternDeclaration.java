package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IExternDeclaration;

/**
 * Extern declaration AST node type.
 *
 * <pre>
 * ExternDeclaration:
 *    { Modifier } <b>extern</b> [ <b>(</b> [ | D | C | C++ | Windows | Pascal ]<b>)</b> ] { Declaration }
 * </pre>
 * 
 * TODO reflect syntax better
 */
public class ExternDeclaration extends Declaration implements IExternDeclaration {
	
	/**
	 * A kind of linkage.
	 */
	public static enum Linkage {
		/** Link to D code by default*/
		DEFAULT,
		/** Link to D code */
		D,
		/** Link to C code */
		C,
		/** Link to C++ code */
		CPP,
		/** Link to Windows code */
		WINDOWS,
		/** Link to Pascal code */
		PASCAL
	}

	/**
	 * The "modifiers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor MODIFIERS_PROPERTY =
	internalModifiersPropertyFactory(ExternDeclaration.class); //$NON-NLS-1$

	/**
	 * The "linkage" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor LINKAGE_PROPERTY =
		new SimplePropertyDescriptor(ExternDeclaration.class, "linkage", Linkage.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "declarations" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor DECLARATIONS_PROPERTY =
		new ChildListPropertyDescriptor(ExternDeclaration.class, "declarations", Declaration.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(3);
		createPropertyList(ExternDeclaration.class, properyList);
		addProperty(MODIFIERS_PROPERTY, properyList);
		addProperty(LINKAGE_PROPERTY, properyList);
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
	 * The modifiers
	 * (element type: <code>Modifier</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList modifiers =
		new ASTNode.NodeList(MODIFIERS_PROPERTY);
	/**
	 * The linkage.
	 */
	private Linkage linkage;

	/**
	 * The declarations
	 * (element type: <code>Declaration</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList declarations =
		new ASTNode.NodeList(DECLARATIONS_PROPERTY);

	/**
	 * Creates a new unparented extern declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ExternDeclaration(AST ast) {
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
		if (property == LINKAGE_PROPERTY) {
			if (get) {
				return getLinkage();
			} else {
				setLinkage((Linkage) value);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetObjectProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final List internalGetChildListProperty(ChildListPropertyDescriptor property) {
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
		final ChildListPropertyDescriptor internalModifiersProperty() {
			return MODIFIERS_PROPERTY;
		}
		
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return EXTERN_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		ExternDeclaration result = new ExternDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.modifiers.addAll(ASTNode.copySubtrees(target, modifiers()));
		result.setLinkage(getLinkage());
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
			acceptChildren(visitor, modifiers());
			acceptChildren(visitor, declarations());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the linkage of this extern declaration.
	 * 
	 * @return the linkage
	 */ 
	public Linkage getLinkage() {
		return this.linkage;
	}

	/**
	 * Sets the linkage of this extern declaration.
	 * 
	 * @param linkage the linkage
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setLinkage(Linkage linkage) {
		if (linkage == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(LINKAGE_PROPERTY);
		this.linkage = linkage;
		postValueChange(LINKAGE_PROPERTY);
	}

	/**
	 * Returns the live ordered list of declarations for this
	 * extern declaration.
	 * 
	 * @return the live list of extern declaration
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
			+ (this.modifiers.listSize())
			+ (this.declarations.listSize())
	;
	}

}
