package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IGotoCaseStatement;

/**
 * Goto case statement AST node type.
 *
 * <pre>
 * GotoCaseStatement:
 *    <b>goto</b> <b>case</b> Identifier <b>;</b>
 * </pre>
 */
public class GotoCaseStatement extends Statement implements IGotoCaseStatement {

	/**
	 * The "label" structural property of this node type.
	 * @since 3.0
	 */
	public static final ChildPropertyDescriptor LABEL_PROPERTY = 
		new ChildPropertyDescriptor(GotoCaseStatement.class, "label", Expression.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;
	
	static {
		List properyList = new ArrayList(2);
		createPropertyList(GotoCaseStatement.class, properyList);
		addProperty(LABEL_PROPERTY, properyList);
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
	 * The label, or <code>null</code> if none; none by default.
	 */
	private Expression label = null;

	/**
	 * Creates a new unparented goto case statement node owned by the given 
	 * AST. By default, the goto case statement has no label.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	GotoCaseStatement(AST ast) {
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
		if (property == LABEL_PROPERTY) {
			if (get) {
				return getLabel();
			} else {
				setLabel((Expression) child);
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
		return GOTO_CASE_STATEMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		GotoCaseStatement result = new GotoCaseStatement(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setLabel((Expression) ASTNode.copySubtree(target, getLabel()));
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
			acceptChild(visitor, getLabel());
		}
		visitor.endVisit(this);
	}
	
	/**
	 * Returns the label of this break statement, or <code>null</code> if
	 * there is none.
	 * 
	 * @return the label, or <code>null</code> if there is none
	 */ 
	public Expression getLabel() {
		return this.label;
	}
	
	/**
	 * Sets or clears the label of this goto case statement.
	 * 
	 * @param label the label, or <code>null</code> if 
	 *    there is none
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * </ul>
	 */ 
	public void setLabel(Expression label) {
		ASTNode oldChild = this.label;
		preReplaceChild(oldChild, label, LABEL_PROPERTY);
		this.label = label;
		postReplaceChild(oldChild, label, LABEL_PROPERTY);
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return super.memSize() + 1 * 4;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.label == null ? 0 : getLabel().treeSize());
	}

	// TODO Descent remove
	public GotoCaseStatement(Expression expr) {
		this.label = expr;
	}

}
