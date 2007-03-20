package descent.internal.compiler.parser;

import java.util.List;


public class ForeachStatement extends Statement {
	
	public TOK op;
	public List<Argument> arguments;
	public Expression aggr;
	public Statement body;

	public ForeachStatement(TOK op, List<Argument> arguments, Expression aggr, Statement body) {
		this.op = op;
		this.arguments = arguments;
		this.aggr = aggr;
		this.body = body;		
	}
	
	@Override
	public int getNodeType() {
		return FOREACH_STATEMENT;
	}

}
