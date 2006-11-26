package descent.internal.core.dom;

public class NullExp extends Expression {

	public NullExp(Loc loc) {
		// TODO Auto-generated constructor stub
	}
	
	public int getElementType() {
		return NULL_EXPRESSION;
	}
	
	@Override
	public String toString() {
		return "null";
	}

}
