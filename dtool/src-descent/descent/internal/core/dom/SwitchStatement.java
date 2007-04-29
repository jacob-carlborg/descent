package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.domX.IASTVisitor;

public class SwitchStatement extends Statement {

	private final Expression expr;
	private final Statement body;

	public SwitchStatement(Expression expr, Statement body) {
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
		return ElementTypes.SWITCH_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, expr);
			TreeVisitor.acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}

}
