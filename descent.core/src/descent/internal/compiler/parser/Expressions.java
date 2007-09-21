package descent.internal.compiler.parser;

// DMD 1.020
public class Expressions extends Array<Expression> {

	private static final long serialVersionUID = 1L;
	
	public Expressions() {
	}
	
	public Expressions(int capacity) {
		super(capacity);
	}
	
	public Expressions(Expressions objects) {
		super(objects);
	}

}
