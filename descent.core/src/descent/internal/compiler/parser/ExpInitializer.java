package descent.internal.compiler.parser;

public class ExpInitializer extends Initializer {

	public Expression exp;

	public ExpInitializer(Expression exp) {
		this.exp = exp;
		this.start = exp.start;
		this.length = exp.length;
	}
	
	@Override
	public ExpInitializer isExpInitializer() {
		return this;
	}
	
	@Override
	public int kind() {
		return EXP_INITIALIZER;
	}

}
