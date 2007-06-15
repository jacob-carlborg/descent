package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IDescentStatement;
import descent.core.domX.IASTVisitor;

public class SwitchStatement extends Statement {

	public final Expression expr;
	public final Statement body;

	public SwitchStatement(Expression expr, Statement body) {
		this.expr = expr;
		this.body = body;
	}
	
	public IExpression getExpression() {
		return expr;
	}
	
	public IDescentStatement getBody() {
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
