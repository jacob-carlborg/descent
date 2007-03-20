package descent.internal.compiler.parser;

public class IfStatement extends Statement {
	
	public Argument arg;
	public Expression condition;
	public Statement ifbody;
	public Statement elsebody;

	public IfStatement(Argument arg, Expression condition, Statement ifbody, Statement elsebody) {
		this.arg = arg;
		this.condition = condition;
		this.ifbody = ifbody;
		this.elsebody = elsebody;		
	}
	
	@Override
	public int getNodeType() {
		return IF_STATEMENT;
	}

}
