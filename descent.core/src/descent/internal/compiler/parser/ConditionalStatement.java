package descent.internal.compiler.parser;

public class ConditionalStatement extends Statement {
	
	public final Condition condition;
	public final Statement ifbody;
	public final Statement elsebody;

	public ConditionalStatement(Condition condition, Statement ifbody, Statement elsebody) {
		this.condition = condition;
		this.ifbody = ifbody;
		this.elsebody = elsebody;		
	}
	
	@Override
	public int kind() {
		return CONDITIONAL_STATEMENT;
	}

}
