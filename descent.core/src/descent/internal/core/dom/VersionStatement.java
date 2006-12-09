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
 *    <b>version</b> [ <b>(</b> Version <b>)</b> ] Statement [ <b>else</b> Statement ]
 * </pre>
 */
public class VersionStatement extends Statement implements IVersionStatement {
	
	/**
	 * The "version" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor VERSION_PROPERTY =
		new ChildPropertyDescriptor(VersionStatement.class, "version", Version.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "thenBody" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor THEN_BODY_PROPERTY =
		new ChildPropertyDescriptor(VersionStatement.class, "thenBody", Statement.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

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
		addProperty(VERSION_PROPERTY, properyList);
		addProperty(THEN_BODY_PROPERTY, properyList);
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
	 * The version.
	 */
	private Version version;

	/**
	 * The thenBody.
	 */
	private Statement thenBody;

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
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == VERSION_PROPERTY) {
			if (get) {
				return getVersion();
			} else {
				setVersion((Version) child);
				return null;
			}
		}
		if (property == THEN_BODY_PROPERTY) {
			if (get) {
				return getThenBody();
			} else {
				setThenBody((Statement) child);
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
	result.setVersion((Version) ASTNode.copySubtree(target, getVersion()));
		result.setThenBody((Statement) getThenBody().clone(target));
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
			acceptChild(visitor, getVersion());
			acceptChild(visitor, getThenBody());
			acceptChild(visitor, getElseBody());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the version of this version statement.
	 * 
	 * @return the version
	 */ 
	public Version getVersion() {
		return this.version;
	}

	/**
	 * Sets the version of this version statement.
	 * 
	 * @param version the version
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setVersion(Version version) {
		ASTNode oldChild = this.version;
		preReplaceChild(oldChild, version, VERSION_PROPERTY);
		this.version = version;
		postReplaceChild(oldChild, version, VERSION_PROPERTY);
	}

	/**
	 * Returns the then body of this version statement.
	 * 
	 * @return the then body
	 */ 
	public Statement getThenBody() {
		if (this.thenBody == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.thenBody == null) {
					preLazyInit();
					this.thenBody = new Block(this.ast);
					postLazyInit(this.thenBody, THEN_BODY_PROPERTY);
				}
			}
		}
		return this.thenBody;
	}

	/**
	 * Sets the then body of this version statement.
	 * 
	 * @param thenBody the then body
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setThenBody(Statement thenBody) {
		if (thenBody == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.thenBody;
		preReplaceChild(oldChild, thenBody, THEN_BODY_PROPERTY);
		this.thenBody = thenBody;
		postReplaceChild(oldChild, thenBody, THEN_BODY_PROPERTY);
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
			+ (this.version == null ? 0 : getVersion().treeSize())
			+ (this.thenBody == null ? 0 : getThenBody().treeSize())
			+ (this.elseBody == null ? 0 : getElseBody().treeSize())
	;
	}

}
