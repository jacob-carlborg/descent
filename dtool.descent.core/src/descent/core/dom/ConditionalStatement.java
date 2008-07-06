package descent.core.dom;


/**
 * Abstract subclass for conditional statements.
 * 
 * ConditionalStatement:
 *    IfStatement
 *    DebugStatement
 *    IftypeStatement
 *    StaticIfStatement
 *    VersionStatement
 */
public abstract class ConditionalStatement extends Statement {
	
	/**
	 * The then body.
	 */
	Statement thenBody;
	
	/**
	 * The else body.
	 */
	Statement elseBody;
	
	/**
	 * Returns structural property descriptor for the "thenBody" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildPropertyDescriptor internalThenBodyProperty();
	
	/**
	 * Returns structural property descriptor for the "elseBody" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildPropertyDescriptor internalElseBodyProperty();
	
	/**
	 * Returns structural property descriptor for the "thenBody" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final ChildPropertyDescriptor getThenBodyProperty() {
		return internalThenBodyProperty();
	}
	
	/**
	 * Returns structural property descriptor for the "elseBody" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final ChildPropertyDescriptor getElseBodyProperty() {
		return internalElseBodyProperty();
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "thenBody" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildPropertyDescriptor internalThenBodyPropertyFactory(Class nodeClass) {
		return new ChildPropertyDescriptor(nodeClass, "thenBody", Statement.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "elseBody" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildPropertyDescriptor internalElseBodyPropertyFactory(Class nodeClass) {
		return new ChildPropertyDescriptor(nodeClass, "elseBody", Statement.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$
	}
	
	/**
	 * Creates a new AST node for an abstract conditional statement.
	 * <p>
	 * N.B. This constructor is package-private; all subclasses must be 
	 * declared in the same package; clients are unable to declare 
	 * additional subclasses.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ConditionalStatement(AST ast) {
		super(ast);
	}
	
	/**
	 * Returns the then body of this conditional  statement.
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
					postLazyInit(this.thenBody, getThenBodyProperty());
				}
			}
		}
		return this.thenBody;
	}

	/**
	 * Sets the then body of this conditional  statement.
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
		preReplaceChild(oldChild, thenBody, getThenBodyProperty());
		this.thenBody = thenBody;
		postReplaceChild(oldChild, thenBody, getThenBodyProperty());
	}

	/**
	 * Returns the else body of this conditional  statement.
	 * 
	 * @return the else body
	 */ 
	public Statement getElseBody() {
		return this.elseBody;
	}

	/**
	 * Sets the else body of this conditional statement.
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
		preReplaceChild(oldChild, elseBody, getElseBodyProperty());
		this.elseBody = elseBody;
		postReplaceChild(oldChild, elseBody, getElseBodyProperty());
	}

}
