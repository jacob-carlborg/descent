package descent.core.dom;



/**
 * Abstract base class of all template parameter AST node types.
 * 
 * <pre>
 * TemplateParameter:
 *    AliasTemplateParameter
 *    TupleTemplateParameter
 *    TypeTemplateParameter
 *    ValueTemplateParameter
 * AliasTemplateParameter:
 *    <b>alias</b> SimpleName [ <b>:</b> Type ] [ <b>=</b> Type ]
 * TupleTemplateParameter:
 *    SimpleName <b>...</b>
 * TypeTemplateParameter:
 *    SimpleName [ <b>:</b> Type ] [ <b>=</b> Type ]
 * ValueTemplateParameter:
 *    Type SimpleName [ <b>:</b> Expression ] [ <b>=</b> Expression ]
 * </pre>
 */
public abstract class TemplateParameter extends ASTNode {
	
	/**
	 * The name.
	 */
	SimpleName name;
	
	/**
	 * Returns structural property descriptor for the "name" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildPropertyDescriptor internalNameProperty();
	
	/**
	 * Returns structural property descriptor for the "name" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final ChildPropertyDescriptor getNameProperty() {
		return internalNameProperty();
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "name" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildPropertyDescriptor internalNamePropertyFactory(Class nodeClass) {
		return new ChildPropertyDescriptor(nodeClass, "name", SimpleName.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$
	}
	
	/**
	 * Creates a new AST node for a template parameter owned by the given AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	TemplateParameter(AST ast) {
		super(ast);
	}
	
	/**
	 * Returns the name of this template parameter.
	 * 
	 * @return the name
	 */ 
	public SimpleName getName() {
		if (this.name == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.name == null) {
					preLazyInit();
					this.name = new SimpleName(this.ast);
					postLazyInit(this.name, getNameProperty());
				}
			}
		}
		return this.name;
	}

	/**
	 * Sets the name of this template parameter.
	 * 
	 * @param name the name
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setName(SimpleName name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.name;
		preReplaceChild(oldChild, name, getNameProperty());
		this.name = name;
		postReplaceChild(oldChild, name, getNameProperty());
	}
	
	/**
	 * Returns whether this template parameter is an alias template parameter
	 * (<code>AliasTemplateParameter</code>). 
	 * 
	 * @return <code>true</code> if this is an alias template parameter, and 
	 *    <code>false</code> otherwise
	 */
	public final boolean isAliasTemplateParameter() {
		return (this instanceof AliasTemplateParameter);
	}
	
	/**
	 * Returns whether this template parameter is a tuple template parameter
	 * (<code>TupleTemplateParameter</code>). 
	 * 
	 * @return <code>true</code> if this is a tuple template parameter, and 
	 *    <code>false</code> otherwise
	 */
	public final boolean isTupleTemplateParameter() {
		return (this instanceof TupleTemplateParameter);
	}
	
	/**
	 * Returns whether this template parameter is a type template parameter
	 * (<code>TypeTemplateParameter</code>). 
	 * 
	 * @return <code>true</code> if this is a type template parameter, and 
	 *    <code>false</code> otherwise
	 */
	public final boolean isTypeTemplateParameter() {
		return (this instanceof TypeTemplateParameter);
	}
	
	/**
	 * Returns whether this template parameter is a value template parameter
	 * (<code>ValueTemplateParameter</code>). 
	 * 
	 * @return <code>true</code> if this is a value template parameter, and 
	 *    <code>false</code> otherwise
	 */
	public final boolean isValueTemplateParameter() {
		return (this instanceof ValueTemplateParameter);
	}

}
