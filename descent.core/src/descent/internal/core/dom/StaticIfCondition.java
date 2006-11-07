package descent.internal.core.dom;

public class StaticIfCondition extends Condition {

	public Expression exp;

	public StaticIfCondition(Loc loc, Expression exp) {
		this.exp = exp;
	}
	
	@Override
	public int getConditionType() {
		return STATIC_IF;
	}

}
