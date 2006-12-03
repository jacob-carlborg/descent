package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IDebugStatement;

/**
 * Debug statement AST node type.
 *
 * <pre>
 * DebugStatement:
 *    <b>debug</b> [ <b>(</b> name <b>)</b> ] Statement [ <b>else</b> Statement ]
 * </pre>
 */
public class DebugStatement extends Statement implements IDebugStatement {
	
	/**
	 * The "name" structural property of this node type.
	 * @since 3.0
	 */
	public static final SimplePropertyDescriptor NAME_PROPERTY = 
		new SimplePropertyDescriptor(DebugStatement.class, "name", String.class, OPTIONAL); //$NON-NLS-1$
	
	/**
	 * The "body" structural property of this node type.
	 * @since 3.0
	 */
	public static final ChildPropertyDescriptor BODY_PROPERTY = 
		new ChildPropertyDescriptor(DebugStatement.class, "body", Statement.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * The "elseBody" structural property of this node type.
	 * @since 3.0
	 */
	public static final ChildPropertyDescriptor ELSE_BODY_PROPERTY = 
		new ChildPropertyDescriptor(DebugStatement.class, "elseBody", Statement.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;
	
	static {
		List properyList = new ArrayList(2);
		createPropertyList(DebugStatement.class, properyList);
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
	 * The name, or <code>null</code> if none; none by default.
	 */
	private String name = null;
	
	/**
	 * The body statement; lazily initialized; defaults to an empty block 
	 * statement.
	 */
	private Statement body = null;
	
	/**
	 * The else body statement; lazily initialized; defaults to an empty block 
	 * statement.
	 */
	private Statement elseBody = null;
	
	/**
	 * Creates a new unparented debug statement node owned by the given 
	 * AST. By default, the name is unspecified, but legal, and
	 * the body and else body statements are empty blocks.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	DebugStatement(AST ast) {
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
		return DEBUG_STATEMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		DebugStatement result = new DebugStatement(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setName(getName());
		result.setBody((Statement) getBody().clone(target));
		result.setElseBody((Statement) getElseBody().clone(target));
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
	 * Returns the name of this debug statement.
	 * 
	 * @return the name
	 */ 
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of this debug statement.
	 * 
	 * @param name the name
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setName(String name) {
		preValueChange(NAME_PROPERTY);
		this.name = name;
		postValueChange(NAME_PROPERTY);
	}
	
	/**
	 * Returns the body of this debug statement.
	 * 
	 * @return the body statement node
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
	 * Sets the body of this debug statement.
	 * 
	 * @param statement the body statement node
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setBody(Statement statement) {
		if (statement == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.body;
		preReplaceChild(oldChild, statement, BODY_PROPERTY);
		this.body = statement;
		postReplaceChild(oldChild, statement, BODY_PROPERTY);
	}
	
	/**
	 * Returns the else body of this debug statement.
	 * 
	 * @return the else body statement node
	 */ 
	public Statement getElseBody() {
		if (this.elseBody == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.elseBody == null) {
					preLazyInit();
					this.elseBody = new Block(this.ast);
					postLazyInit(this.elseBody, ELSE_BODY_PROPERTY);
				}
			}
		}
		return this.elseBody;
	}
	
	/**
	 * Sets the else body of this debug statement.
	 * 
	 * @param statement the else body statement node
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setElseBody(Statement statement) {
		if (statement == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.elseBody;
		preReplaceChild(oldChild, statement, ELSE_BODY_PROPERTY);
		this.elseBody = statement;
		postReplaceChild(oldChild, statement, ELSE_BODY_PROPERTY);
	}

	public DebugStatement(String name, Statement body, Statement elseBody) {
		this.name = name;
		this.body = body;
		this.elseBody = elseBody;
	}

}
