package descent.internal.compiler.parser;

public class StaticIfCondition extends Condition {

	public Expression exp;

	public StaticIfCondition(Expression exp) {
		this.exp = exp;
	}
	
	@Override
	public int getConditionType() {
		return STATIC_IF;
	}

}
