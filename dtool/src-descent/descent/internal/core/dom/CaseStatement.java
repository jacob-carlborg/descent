package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.ICaseStatement;
import descent.core.dom.IExpression;
import descent.core.dom.IDescentStatement;
import descent.core.domX.IASTVisitor;

public class CaseStatement extends Statement implements ICaseStatement {

	public final Expression exp;
	public final Statement s;

	public CaseStatement(Expression exp, Statement s) {
		this.exp = exp;
		this.s = s;
	}
	
	public IExpression getExpression() {
		return exp;
	}
	
	public IDescentStatement getStatement() {
		return s;
	}
	
	public int getElementType() {
		return ElementTypes.CASE_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, exp);
			TreeVisitor.acceptChild(visitor, s);
		}
		visitor.endVisit(this);
	}

}
