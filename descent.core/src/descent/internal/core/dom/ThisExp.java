package descent.internal.core.dom;

public class ThisExp extends Expression {

	public ThisExp(Loc loc) {
		// TODO Auto-generated constructor stub
	}
	
	public int getExpressionType() {
		return EXPRESSION_THIS;
	}
	
	@Override
	public String toString() {
		return "this";
	}

}
