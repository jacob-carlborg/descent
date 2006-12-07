package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IDeclarationStatement;

/**
 * A statement that wraps a declaration.
 */
public class DeclarationStatement extends Statement implements IDeclarationStatement {

	/**
	 * The "declaration" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor DECLARATION_PROPERTY =
		new ChildPropertyDescriptor(DeclarationStatement.class, "declaration", Declaration.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(1);
		createPropertyList(DeclarationStatement.class, properyList);
		addProperty(DECLARATION_PROPERTY, properyList);
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
	 * The declaration.
	 */
	private Declaration declaration;


	/**
	 * Creates a new unparented declaration statement node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	DeclarationStatement(AST ast) {
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
		if (property == DECLARATION_PROPERTY) {
			if (get) {
				return getDeclaration();
			} else {
				setDeclaration((Declaration) child);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetChildProperty(property, get, child);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return DECLARATION_STATEMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		DeclarationStatement result = new DeclarationStatement(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setDeclaration((Declaration) getDeclaration().clone(target));
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
			acceptChild(visitor, getDeclaration());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the declaration of this declaration statement.
	 * 
	 * @return the declaration
	 */ 
	public Declaration getDeclaration() {
		if (this.declaration == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.declaration == null) {
					preLazyInit();
					this.declaration = new EnumDeclaration(this.ast);
					postLazyInit(this.declaration, DECLARATION_PROPERTY);
				}
			}
		}
		return this.declaration;
	}

	/**
	 * Sets the declaration of this declaration statement.
	 * 
	 * @param declaration the declaration
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setDeclaration(Declaration declaration) {
		if (declaration == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.declaration;
		preReplaceChild(oldChild, declaration, DECLARATION_PROPERTY);
		this.declaration = declaration;
		postReplaceChild(oldChild, declaration, DECLARATION_PROPERTY);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 1 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.declaration == null ? 0 : getDeclaration().treeSize())
	;
	}

}
