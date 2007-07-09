package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IGotoCaseStatement;
import descent.core.domX.IASTVisitor;

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
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
