package descent.internal.compiler.parser;

// DMD 1.020
public class Identifiers extends Array<IdentifierExp> {

	private static final long serialVersionUID = 1L;
	
	public Identifiers() {
	}
	
	public Identifiers(int capacity) {
		super(capacity);
	}
	
	public Identifiers(Identifiers elements) {
		super(elements);
	}

}
