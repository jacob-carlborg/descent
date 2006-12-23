package descent.internal.core.dom;

import descent.core.dom.IType;

/**
 * Abstract subclass for types.
 * <pre>
 * Type:
 *    ArrayType
 *    DelegateType
 *    PointerType
 *    PrimitiveType
 *    QualifiedType
 *    SimpleType
 *    TemplateType
 *    TypeofType
 * </pre>
 */
public abstract class Type extends ASTNode implements IType {
	
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

}
