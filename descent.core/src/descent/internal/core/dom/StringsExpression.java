package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;

/**
 * A strings expression is a list of ajacent StringLiterals.
 * 
 * <pre>
 * StringsExpression:
 *    { StringLiteral }
 * </pre>
 * 
 * <p>The ASTParser will try to return StringLiteral instead of a StringsExpression
 * containing a single StringLiteral when possible.</p>
 */
public class StringsExpression extends Expression {
	
	/**
	 * The "stringLiterals" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor STRING_LITERALS_PROPERTY =
		new ChildListPropertyDescriptor(StringsExpression.class, "stringLiterals", StringLiteral.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(1);
		createPropertyList(StringsExpression.class, properyList);
		addProperty(STRING_LITERALS_PROPERTY, properyList);
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
	 * The string literals
	 * (element type: <code>StringLiteral</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList stringLiterals =
		new ASTNode.NodeList(STRING_LITERALS_PROPERTY);

	/**
	 * Creates a new unparented strings expression node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	StringsExpression(AST ast) {
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
		if (property == STRING_LITERALS_PROPERTY) {
			return stringLiterals();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return STRINGS_EXPRESSION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		StringsExpression result = new StringsExpression(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.stringLiterals.addAll(ASTNode.copySubtrees(target, stringLiterals()));
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
			acceptChildren(visitor, stringLiterals());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the live ordered list of string literals for this
	 * strings expression.
	 * 
	 * @return the live list of strings expression
	 *    (element type: <code>StringLiteral</code>)
	 */ 
	public List<StringLiteral> stringLiterals() {
		return this.stringLiterals;
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
			+ (this.stringLiterals.listSize())
	;
	}

}
