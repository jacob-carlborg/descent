package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Struct expression AST node. This node is the compile-time representation
 * of a {@link StructInitializer}.
 * 
 * <pre>
 * StructExpression:
 *    <b>{</b> [ Expression { <b>,</b> Expression } ] <b>}</b> 
 * </pre>
 */
public class StructExpression extends Expression {
	
	/**
	 * The "expressions" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor EXPRESSIONS_PROPERTY =
		new ChildListPropertyDescriptor(StructExpression.class, "expressions", Expression.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(1);
		createPropertyList(StructExpression.class, properyList);
		addProperty(EXPRESSIONS_PROPERTY, properyList);
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
	 * The fragments
	 * (element type: <code>StructInitializerFragment</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList expressions =
		new ASTNode.NodeList(EXPRESSIONS_PROPERTY);

	/**
	 * Creates a new unparented struct expression node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	StructExpression(AST ast) {
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
		if (property == EXPRESSIONS_PROPERTY) {
			return expressions();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return STRUCT_EXPRESSION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		StructExpression result = new StructExpression(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.expressions.addAll(ASTNode.copySubtrees(target, expressions()));
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
			acceptChildren(visitor, expressions);
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the live ordered list of fragments for this
	 * struct initializer.
	 * 
	 * @return the live list of struct initializer
	 *    (element type: <code>StructInitializerFragment</code>)
	 */ 
	public List<Expression> expressions() {
		return this.expressions;
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
			+ (this.expressions.listSize())
	;
	}

}
