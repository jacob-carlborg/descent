package descent.internal.compiler.parser;

public class ConditionalStatement extends Statement {
	
	public final Condition condition;
	public final Statement ifbody;
	public final Statement elsebody;

	public ConditionalStatement(Loc loc, Condition condition, Statement ifbody, Statement elsebody) {
		super(loc);
		this.condition = condition;
		this.ifbody = ifbody;
		this.elsebody = elsebody;		
	}
	
	@Override
	public int getNodeType() {
		return CONDITIONAL_STATEMENT;
	}

}
