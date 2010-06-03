package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Switch case statement AST node type.
 *
 * <pre>
 * SwitchCase:
 *    <b>case</b> Expression { <b>,</b> Expression } <b>:</b> { Statement }
 * </pre>
 */
public class SwitchCase extends Statement {

	/**
	 * The "expressions" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor EXPRESSIONS_PROPERTY =
		new ChildListPropertyDescriptor(SwitchCase.class, "expressions", Expression.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "statements" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor STATEMENTS_PROPERTY =
		new ChildListPropertyDescriptor(SwitchCase.class, "statements", Statement.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(SwitchCase.class, properyList);
		addProperty(EXPRESSIONS_PROPERTY, properyList);
		addProperty(STATEMENTS_PROPERTY, properyList);
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
	 * The expressions
	 * (element type: <code>Expression</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList expressions =
		new ASTNode.NodeList(EXPRESSIONS_PROPERTY);
	/**
	 * The statements
	 * (element type: <code>Statement</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList statements =
		new ASTNode.NodeList(STATEMENTS_PROPERTY);

	/**
	 * Creates a new unparented switch case node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	SwitchCase(AST ast) {
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
		if (property == STATEMENTS_PROPERTY) {
			return statements();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return SWITCH_CASE;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		SwitchCase result = new SwitchCase(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.expressions.addAll(ASTNode.copySubtrees(target, expressions()));
		result.statements.addAll(ASTNode.copySubtrees(target, statements()));
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
			acceptChildren(visitor, this.expressions);
			acceptChildren(visitor, this.statements);
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the live ordered list of expressions for this
	 * switch case.
	 * 
	 * @return the live list of switch case
	 *    (element type: <code>Expression</code>)
	 */ 
	public List<Expression> expressions() {
		return this.expressions;
	}

	/**
	 * Returns the live ordered list of statements for this
	 * switch case.
	 * 
	 * @return the live list of switch case
	 *    (element type: <code>Statement</code>)
	 */ 
	public List<Statement> statements() {
		return this.statements;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 2 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.expressions.listSize())
			+ (this.statements.listSize())
	;
	}

}
