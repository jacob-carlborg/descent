package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.dom.ISwitchStatement;

public class SwitchStatement extends Statement implements ISwitchStatement {

	private final Expression expr;
	private final Statement body;

	public SwitchStatement(Loc loc, Expression expr, Statement body) {
		this.expr = expr;
		this.body = body;
	}
	
	public IExpression getExpression() {
		return expr;
	}
	
	public IStatement getBody() {
		return body;
	}
	
	public int getElementType() {
		return SWITCH_STATEMENT;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, expr);
			acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}

}
