package descent.internal.core.dom;

import descent.core.dom.ITemplateParameter;

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
public abstract class TemplateParameter extends ASTNode implements ITemplateParameter {
	
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
