package descent.internal.compiler.parser;

// DMD 1.020
public class Statements extends Array<Statement> {

	private static final long serialVersionUID = 1L;
	
	public Statements() {
	}
	
	public Statements(int capacity) {
		super(capacity);
	}
	
	public Statements(Statements elements) {
		super(elements);
	}

}
