package descent.internal.core.dom;

public class IftypeCondition extends Condition {

	public IftypeCondition(Loc loc, Type targ, Identifier[] ident, TOK tok, Type tspec) {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int getConditionType() {
		return IF_TYPE;
	}

}
