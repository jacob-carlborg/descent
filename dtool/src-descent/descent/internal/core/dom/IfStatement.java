package descent.internal.core.dom;

import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.domX.ASTVisitor;

public class IfStatement extends Statement {

	private final Argument arg;
	private final Expression expr;
	private final Statement ifbody;
	private final Statement elsebody;

	public IfStatement(Argument arg, Expression expr, Statement ifbody, Statement elsebody) {
		this.arg = arg;
		this.expr = expr;
		this.ifbody = ifbody;
		this.elsebody = elsebody;
	}
	
	public int getElementType() {
		return ElementTypes.IF_STATEMENT;
	}
	
	public Argument getArgument() {
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
	
	public void accept0(ASTVisitor visitor) {
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
