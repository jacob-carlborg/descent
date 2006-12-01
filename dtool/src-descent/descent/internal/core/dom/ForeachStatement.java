package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IArgument;
import descent.core.dom.IExpression;
import descent.core.dom.IForeachStatement;
import descent.core.dom.IStatement;
import descent.core.domX.ASTVisitor;

public class ForeachStatement extends Statement implements IForeachStatement {
	
	private IArgument[] arguments;
	private Expression aggr;
	private Statement body;
	private boolean reverse;

	public ForeachStatement(TOK op, List<Argument> arguments, Expression aggr, Statement body) {
		this.body = body;
		this.arguments = arguments.toArray(new IArgument[arguments.size()]);
		this.aggr = aggr;
		this.reverse = op == TOK.TOKforeach_reverse;
	}
	
	public IArgument[] getArguments() {
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
		return FOREACH_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, arguments);
			acceptChild(visitor, aggr);
			acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}

}