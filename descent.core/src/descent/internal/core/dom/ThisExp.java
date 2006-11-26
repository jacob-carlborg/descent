package descent.internal.core.dom;

public class ThisExp extends Expression {

	public ThisExp(Loc loc) {
		// TODO Auto-generated constructor stub
	}
	
	public int getElementType() {
		return THIS_EXPRESSION;
	}
	
	@Override
	public String toString() {
		return "this";
	}

}
