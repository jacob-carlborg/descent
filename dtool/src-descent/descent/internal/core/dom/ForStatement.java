package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IForStatement;
import descent.core.dom.IDescentStatement;
import descent.core.domX.IASTVisitor;

public class ForStatement extends Statement implements IForStatement {

	public final Statement init;
	public final Expression condition;
	public final Expression increment;
	public final Statement body;

	public ForStatement(Statement init, Expression condition, Expression increment, Statement body) {
		this.init = init;
		this.condition = condition;
		this.increment = increment;
		this.body = body;
	}
	
	public IDescentStatement getInitializer() {
		return init;
	}
	
	public IExpression getCondition() {
		return condition;
	}
	
	public IExpression getIncrement() {
		return increment;
	}
	
	public IDescentStatement getBody() {
		return body;
	}
	
	public int getElementType() {
		return ElementTypes.FOR_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, init);
			TreeVisitor.acceptChild(visitor, condition);
			TreeVisitor.acceptChild(visitor, increment);
			TreeVisitor.acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}

}
