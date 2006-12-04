package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IVersionStatement;

/**
 * Version statement AST node type.
 *
 * <pre>
 * VersionStatement:
 *    <b>version</b> [ <b>(</b> name <b>)</b> ] Statement [ <b>else</b> Statement ]
 * </pre>
 */
public class VersionStatement extends Statement implements IVersionStatement {
	
	/**
	 * The "name" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor NAME_PROPERTY =
		new SimplePropertyDescriptor(VersionStatement.class, "name", String.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "body" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor BODY_PROPERTY =
		new ChildPropertyDescriptor(VersionStatement.class, "body", Statement.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "elseBody" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor ELSE_BODY_PROPERTY =
		new ChildPropertyDescriptor(VersionStatement.class, "elseBody", Statement.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(3);
		createPropertyList(VersionStatement.class, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(BODY_PROPERTY, properyList);
		addProperty(ELSE_BODY_PROPERTY, properyList);
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
	 * The name.
	 */
	private String name;

	/**
	 * The body.
	 */
	private Statement body;

	/**
	 * The elseBody.
	 */
	private Statement elseBody;


	/**
	 * Creates a new unparented version statement node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	VersionStatement(AST ast) {
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
		if (property == NAME_PROPERTY) {
			if (get) {
				return getName();
			} else {
				setName((String) value);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetObjectProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == BODY_PROPERTY) {
			if (get) {
				return getBody();
			} else {
				setBody((Statement) child);
				return null;
			}
		}
		if (property == ELSE_BODY_PROPERTY) {
			if (get) {
				return getElseBody();
			} else {
				setElseBody((Statement) child);
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
		return VERSION_STATEMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		VersionStatement result = new VersionStatement(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setName(getName());
		result.setBody((Statement) getBody().clone(target));
	result.setElseBody((Statement) ASTNode.copySubtree(target, getElseBody()));
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
			acceptChild(visitor, getBody());
			acceptChild(visitor, getElseBody());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the name of this version statement.
	 * 
	 * @return the name
	 */ 
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of this version statement.
	 * 
	 * @param name the name
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setName(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(NAME_PROPERTY);
		this.name = name;
		postValueChange(NAME_PROPERTY);
	}

	/**
	 * Returns the body of this version statement.
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
	 * Sets the body of this version statement.
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

	/**
	 * Returns the else body of this version statement.
	 * 
	 * @return the else body
	 */ 
	public Statement getElseBody() {
		return this.elseBody;
	}

	/**
	 * Sets the else body of this version statement.
	 * 
	 * @param elseBody the else body
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setElseBody(Statement elseBody) {
		if (elseBody == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.elseBody;
		preReplaceChild(oldChild, elseBody, ELSE_BODY_PROPERTY);
		this.elseBody = elseBody;
		postReplaceChild(oldChild, elseBody, ELSE_BODY_PROPERTY);
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
			+ (this.body == null ? 0 : getBody().treeSize())
			+ (this.elseBody == null ? 0 : getElseBody().treeSize())
	;
	}

	public VersionStatement(String name, Statement body, Statement elseBody) {
		this.name = name;
		this.body = body;
		this.elseBody = elseBody;
	}

}
