package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IForStatement;
import descent.core.dom.IStatement;

public class ForStatement extends Statement implements IForStatement {

	private final Statement init;
	private final Expression condition;
	private final Expression increment;
	private final Statement body;

	public ForStatement(Statement init, Expression condition, Expression increment, Statement body) {
		this.init = init;
		this.condition = condition;
		this.increment = increment;
		this.body = body;
	}
	
	public IStatement getInitializer() {
		return init;
	}
	
	public IExpression getCondition() {
		return condition;
	}
	
	public IExpression getIncrement() {
		return increment;
	}
	
	public IStatement getBody() {
		return body;
	}
	
	public int getNodeType0() {
		return FOR_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, init);
			acceptChild(visitor, condition);
			acceptChild(visitor, increment);
			acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}

}
