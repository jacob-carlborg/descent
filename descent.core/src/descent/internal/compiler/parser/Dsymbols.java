package descent.internal.compiler.parser;

import java.util.ArrayList;

public class Dsymbols extends ArrayList<Dsymbol> {

	private static final long serialVersionUID = 1L;
	
	public Dsymbols() {
	}
	
	public Dsymbols(int capacity) {
		super(capacity);
	}
	
	public Dsymbols(Dsymbols elements) {
		super(elements);
	}

}
