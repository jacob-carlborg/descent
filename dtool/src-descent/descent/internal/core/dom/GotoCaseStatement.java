package descent.internal.core.dom;

import descent.core.dom.IExpression;
import descent.core.dom.IGotoCaseStatement;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class GotoCaseStatement extends Statement implements IGotoCaseStatement {

	private final Expression exp;

	public GotoCaseStatement(Expression exp) {
		this.exp = exp;
	}
	
	public int getElementType() {
		return ElementTypes.GOTO_CASE_STATEMENT;
	}
	
	public IExpression getCase() {
		return exp;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
