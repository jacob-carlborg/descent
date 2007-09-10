package descent.internal.compiler.parser;

import java.util.ArrayList;

public class Identifiers extends ArrayList<IdentifierExp> {

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
