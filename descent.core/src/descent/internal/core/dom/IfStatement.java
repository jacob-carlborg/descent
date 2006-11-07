package descent.internal.core.dom;

import descent.core.dom.IArgument;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IIfStatement;
import descent.core.dom.IStatement;

public class IfStatement extends Statement implements IIfStatement {

	private final Argument arg;
	private final Expression expr;
	private final Statement ifbody;
	private final Statement elsebody;

	public IfStatement(Loc loc, Argument arg, Expression expr, Statement ifbody, Statement elsebody) {
		this.arg = arg;
		this.expr = expr;
		this.ifbody = ifbody;
		this.elsebody = elsebody;
	}
	
	public int getStatementType() {
		return STATEMENT_IF;
	}
	
	public IArgument getArgument() {
		return arg;
	}
	
	public IExpression getCondition() {
		return expr;
	}
	
	public IStatement getThen() {
		return ifbody;
	}
	
	public IStatement getElse() {
		return elsebody;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, arg);
			acceptChild(visitor, expr);
			acceptChild(visitor, ifbody);
			acceptChild(visitor, elsebody);
		}
		visitor.endVisit(this);
	}

}
