package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.dom.IStaticIfStatement;

public class StaticIfStatement extends Statement implements IStaticIfStatement {
	
	private final Condition condition;
	private final Statement ifbody;
	private final Statement elsebody;

	public StaticIfStatement(Condition condition, Statement ifbody, Statement elsebody) {
		this.condition = condition;
		this.ifbody = ifbody;
		this.elsebody = elsebody;
	}
	
	public IExpression getCondition() {
		return ((StaticIfCondition) condition).exp;
	}
	
	public IStatement getBody() {
		return ifbody;
	}
	
	public IStatement getElseBody() {
		return elsebody;
	}
	
	public int getNodeType0() {
		return STATIC_IF_STATEMENT;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		boolean children;
		children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ((StaticIfCondition) condition).exp);
			acceptChild(visitor, ifbody);
			acceptChild(visitor, elsebody);
		}
		visitor.endVisit(this);
	}

}
