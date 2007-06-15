package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IDescentStatement;
import descent.core.domX.IASTVisitor;

public class IfStatement extends Statement {

	public final Argument arg;
	public final Expression expr;
	public final Statement ifbody;
	public final Statement elsebody;

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
	
	public IDescentStatement getThen() {
		return ifbody;
	}
	
	public IDescentStatement getElse() {
		return elsebody;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, arg);
			TreeVisitor.acceptChild(visitor, expr);
			TreeVisitor.acceptChild(visitor, ifbody);
			TreeVisitor.acceptChild(visitor, elsebody);
		}
		visitor.endVisit(this);
	}

}
