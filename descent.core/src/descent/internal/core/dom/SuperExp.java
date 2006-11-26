package descent.internal.core.dom;

public class SuperExp extends Expression {

	public SuperExp(Loc loc) {
		// TODO Auto-generated constructor stub
	}
	
	public int getElementType() {
		return SUPER_EXPRESSION;
	}
	
	@Override
	public String toString() {
		return "super";
	}

}
