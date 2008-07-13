package descent.core.dom;


/**
 * This template parameter AST node type.
 *
 * <pre>
 * ThisTemplateParameter:
 *    <b>this</b>  SimpleName [ <b>:</b> Type ] [ <b>=</b> Type ]
 * </pre>
 */
public class ThisTemplateParameter extends TypeTemplateParameter {

	ThisTemplateParameter(AST ast) {
		super(ast);
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return THIS_TEMPLATE_PARAMETER;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	void accept0(ASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if (visitChildren) {
			// visit children in normal left to right reading order
			acceptChild(visitor, getName());
			acceptChild(visitor, getSpecificType());
			acceptChild(visitor, getDefaultType());
		}
		visitor.endVisit(this);
	}
	
	@Override
	boolean subtreeMatch0(ASTMatcher matcher, Object other) {
//		 dispatch to correct overloaded match method
		return matcher.match(this, other);
	}
	
}
