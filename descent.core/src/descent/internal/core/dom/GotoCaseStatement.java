package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IGotoCaseStatement;

public class GotoCaseStatement extends Statement implements IGotoCaseStatement {

	private final Expression exp;

	public GotoCaseStatement(Loc loc, Expression exp) {
		this.exp = exp;
	}
	
	public int getElementType() {
		return GOTO_CASE_STATEMENT;
	}
	
	public IExpression getCase() {
		return exp;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
