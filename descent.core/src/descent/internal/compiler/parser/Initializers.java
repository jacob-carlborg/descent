package descent.internal.compiler.parser;

import java.util.ArrayList;

public class Initializers extends ArrayList<Initializer> {

	private static final long serialVersionUID = 1L;
	
	public Initializers() {
	}
	
	public Initializers(int capacity) {
		super(capacity);
	}
	
	public Initializers(Initializers elements) {
		super(elements);
	}

}
