package descent.internal.core.dom;

import java.util.List;

import util.tree.TreeVisitor;

import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.domX.IASTVisitor;

public class ForeachStatement extends Statement implements IStatement {
	
	public Argument[] arguments;
	public Expression aggr;
	public Statement body;
	public boolean reverse;

	public ForeachStatement(TOK op, List<Argument> arguments, Expression aggr, Statement body) {
		this.body = body;
		this.arguments = arguments.toArray(new Argument[arguments.size()]);
		this.aggr = aggr;
		this.reverse = op == TOK.TOKforeach_reverse;
	}
	
	public Argument[] getArguments() {
		return arguments;
	}
	
	public IExpression getIterable() {
		return aggr;
	}
	
	public IStatement getBody() {
		return body;
	}
	
	public boolean isReverse() {
		return reverse;
	}
	
	public int getElementType() {
		return ElementTypes.FOREACH_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, arguments);
			TreeVisitor.acceptChild(visitor, aggr);
			TreeVisitor.acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}

}
