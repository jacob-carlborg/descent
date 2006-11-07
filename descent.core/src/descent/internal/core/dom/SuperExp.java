package descent.internal.core.dom;

public class SuperExp extends Expression {

	public SuperExp(Loc loc) {
		// TODO Auto-generated constructor stub
	}
	
	public int getExpressionType() {
		return EXPRESSION_SUPER;
	}
	
	@Override
	public String toString() {
		return "super";
	}

}
