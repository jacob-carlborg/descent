package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Asm statement AST node.
 *
 *<pre>
 *AsmStatement:
 *   { AsmToken } <b>;</b>
 *</pre>
 */
public class AsmStatement extends Statement {

	/**
	 * The "tokens" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor TOKENS_PROPERTY =
		new ChildListPropertyDescriptor(AsmStatement.class, "tokens", AsmToken.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(1);
		createPropertyList(AsmStatement.class, properyList);
		addProperty(TOKENS_PROPERTY, properyList);
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
	 * The tokens
	 * (element type: <code>AsmToken</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList tokens =
		new ASTNode.NodeList(TOKENS_PROPERTY);

	/**
	 * Creates a new unparented asm statement node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	AsmStatement(AST ast) {
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
	final List internalGetChildListProperty(ChildListPropertyDescriptor property) {
		if (property == TOKENS_PROPERTY) {
			return tokens();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return ASM_STATEMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		AsmStatement result = new AsmStatement(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.tokens.addAll(ASTNode.copySubtrees(target, tokens()));
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
			acceptChildren(visitor, this.tokens);
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the live ordered list of tokens for this
	 * asm statement.
	 * 
	 * @return the live list of asm statement
	 *    (element type: <code>AsmToken</code>)
	 */ 
	public List<AsmToken> tokens() {
		return this.tokens;
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
			+ (this.tokens.listSize())
	;
	}

}
