package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IImportDeclaration;

// TODO comment
public class ImportDeclaration extends Declaration implements IImportDeclaration {
	
	/**
	 * The "modifierFlags" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor MODIFIER_FLAGS_PROPERTY =
		new SimplePropertyDescriptor(ImportDeclaration.class, "modifierFlags", int.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "static" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor STATIC_PROPERTY =
		new SimplePropertyDescriptor(ImportDeclaration.class, "static", boolean.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "imports" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor IMPORTS_PROPERTY =
		new ChildListPropertyDescriptor(ImportDeclaration.class, "imports", Import.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(3);
		createPropertyList(ImportDeclaration.class, properyList);
		addProperty(MODIFIER_FLAGS_PROPERTY, properyList);
		addProperty(STATIC_PROPERTY, properyList);
		addProperty(IMPORTS_PROPERTY, properyList);
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
	 * The static.
	 */
	private boolean isStatic;

	/**
	 * The imports
	 * (element type: <code>Import</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList imports =
		new ASTNode.NodeList(IMPORTS_PROPERTY);

	/**
	 * Creates a new unparented import declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ImportDeclaration(AST ast) {
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
	final boolean internalGetSetBooleanProperty(SimplePropertyDescriptor property, boolean get, boolean value) {
		if (property == STATIC_PROPERTY) {
			if (get) {
				return isStatic();
			} else {
				setStatic(value);
				return false;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetBooleanProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final List internalGetChildListProperty(ChildListPropertyDescriptor property) {
		if (property == IMPORTS_PROPERTY) {
			return imports();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return IMPORT_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		ImportDeclaration result = new ImportDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setModifierFlags(getModifierFlags());
		result.setStatic(isStatic());
		result.imports.addAll(ASTNode.copySubtrees(target, imports()));
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
			acceptChildren(visitor, imports());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the modifier flags of this import declaration.
	 * 
	 * @return the modifier flags
	 */ 
	public int getModifierFlags() {
		return this.modifierFlags;
	}

	/**
	 * Sets the modifier flags of this import declaration.
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
	 * Returns the static of this import declaration.
	 * 
	 * @return the static
	 */ 
	public boolean isStatic() {
		return this.isStatic;
	}

	/**
	 * Sets the static of this import declaration.
	 * 
	 * @param static the static
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setStatic(boolean isStatic) {
		preValueChange(STATIC_PROPERTY);
		this.isStatic = isStatic;
		postValueChange(STATIC_PROPERTY);
	}

	/**
	 * Returns the live ordered list of imports for this
	 * import declaration.
	 * 
	 * @return the live list of import declaration
	 *    (element type: <code>Import</code>)
	 */ 
	public List<Import> imports() {
		return this.imports;
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
			+ (this.imports.listSize())
	;
	}

}
