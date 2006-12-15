package descent.internal.core.dom;

import descent.core.dom.ICaseStatement;
import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.domX.ASTVisitor;

public class CaseStatement extends Statement implements ICaseStatement {

	private final Expression exp;
	private final Statement s;

	public CaseStatement(Expression exp, Statement s) {
		this.exp = exp;
		this.s = s;
	}
	
	public IExpression getExpression() {
		return exp;
	}
	
	public IStatement getStatement() {
		return s;
	}
	
	public int getElementType() {
		return ElementTypes.CASE_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
			acceptChild(visitor, s);
		}
		visitor.endVisit(this);
	}

}
