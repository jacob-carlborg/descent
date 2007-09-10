package descent.internal.compiler.parser;

import java.util.ArrayList;

public class Statements extends ArrayList<Statement> {

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
