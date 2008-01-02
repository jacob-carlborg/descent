package descent.core.dom;

/**
 * Abstract subclass for types.
 * <pre>
 * Type:
 *    ArrayType
 *    DelegateType
 *    ModifiedType
 *    PointerType
 *    PrimitiveType
 *    QualifiedType
 *    SimpleType
 *    TemplateType
 *    TypeofType
 * </pre>
 */
public abstract class Type extends ASTNode {
	
	/**
	 * Creates a new AST node for an abstract type.
	 * <p>
	 * N.B. This constructor is package-private; all subclasses must be 
	 * declared in the same package; clients are unable to declare 
	 * additional subclasses.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	Type(AST ast) {
		super(ast);
	}
	
	/**
	 * Resolves and returns the binding for this type.
	 * <p>
	 * Note that bindings are generally unavailable unless requested when the
	 * AST is being built.
	 * </p>
	 * 
	 * @return the type binding, or <code>null</code> if the binding cannot be 
	 *    resolved
	 */	
	public final IBinding resolveBinding() {
		return this.ast.getBindingResolver().resolveType(this);
	}

}
