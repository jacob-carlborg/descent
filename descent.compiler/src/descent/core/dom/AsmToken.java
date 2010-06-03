package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Asm token AST node.
 */
public class AsmToken extends ASTNode {
	
	/**
	 * The "token" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor TOKEN_PROPERTY =
		new SimplePropertyDescriptor(AsmToken.class, "token", String.class, MANDATORY); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(1);
		createPropertyList(AsmToken.class, properyList);
		addProperty(TOKEN_PROPERTY, properyList);
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
	 * The token.
	 */
	private String token;


	/**
	 * Creates a new unparented asm token node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	AsmToken(AST ast) {
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
		if (property == TOKEN_PROPERTY) {
			if (get) {
				return getToken();
			} else {
				setToken((String) value);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetObjectProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return ASM_TOKEN;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		AsmToken result = new AsmToken(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setToken(getToken());
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
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the token of this asm token.
	 * 
	 * @return the token
	 */ 
	public String getToken() {
		return this.token;
	}

	/**
	 * Sets the token of this asm token.
	 * 
	 * @param token the token
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setToken(String token) {
		if (token == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(TOKEN_PROPERTY);
		this.token = token;
		postValueChange(TOKEN_PROPERTY);
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
	;
	}

}
