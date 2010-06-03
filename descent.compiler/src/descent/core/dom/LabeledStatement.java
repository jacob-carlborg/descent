package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * A statement that is labeled.
 * <pre>
 * LabeledStatement:
 *    SimpleName <b>:</b> Statement
 * </pre>
 */
public class LabeledStatement extends Statement {
	
	/**
	 * The "label" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor LABEL_PROPERTY =
		new ChildPropertyDescriptor(LabeledStatement.class, "label", SimpleName.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "body" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor BODY_PROPERTY =
		new ChildPropertyDescriptor(LabeledStatement.class, "body", Statement.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(LabeledStatement.class, properyList);
		addProperty(LABEL_PROPERTY, properyList);
		addProperty(BODY_PROPERTY, properyList);
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
	 * The label.
	 */
	private SimpleName label;

	/**
	 * The body.
	 */
	private Statement body;


	/**
	 * Creates a new unparented label statement node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	LabeledStatement(AST ast) {
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
				setLabel((SimpleName) child);
				return null;
			}
		}
		if (property == BODY_PROPERTY) {
			if (get) {
				return getBody();
			} else {
				setBody((Statement) child);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetChildProperty(property, get, child);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return LABELED_STATEMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		LabeledStatement result = new LabeledStatement(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setLabel((SimpleName) getLabel().clone(target));
		result.setBody((Statement) getBody().clone(target));
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
			acceptChild(visitor, getLabel());
			acceptChild(visitor, getBody());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the label of this label statement.
	 * 
	 * @return the label
	 */ 
	public SimpleName getLabel() {
		if (this.label == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.label == null) {
					preLazyInit();
					this.label = new SimpleName(this.ast);
					postLazyInit(this.label, LABEL_PROPERTY);
				}
			}
		}
		return this.label;
	}

	/**
	 * Sets the label of this label statement.
	 * 
	 * @param label the label
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setLabel(SimpleName label) {
		if (label == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.label;
		preReplaceChild(oldChild, label, LABEL_PROPERTY);
		this.label = label;
		postReplaceChild(oldChild, label, LABEL_PROPERTY);
	}

	/**
	 * Returns the body of this label statement.
	 * 
	 * @return the body
	 */ 
	public Statement getBody() {
		if (this.body == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.body == null) {
					preLazyInit();
					this.body = new Block(this.ast);
					postLazyInit(this.body, BODY_PROPERTY);
				}
			}
		}
		return this.body;
	}

	/**
	 * Sets the body of this label statement.
	 * 
	 * @param body the body
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setBody(Statement body) {
		if (body == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.body;
		preReplaceChild(oldChild, body, BODY_PROPERTY);
		this.body = body;
		postReplaceChild(oldChild, body, BODY_PROPERTY);
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
			+ (this.label == null ? 0 : getLabel().treeSize())
			+ (this.body == null ? 0 : getBody().treeSize())
	;
	}

}
