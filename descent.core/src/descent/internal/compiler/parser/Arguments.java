package descent.internal.compiler.parser;

import java.util.ArrayList;

public class Arguments extends ArrayList<Argument> {

	private static final long serialVersionUID = 1L;
	
	public Arguments() {
	}
	
	public Arguments(int capacity) {
		super(capacity);
	}
	
	public Arguments(Arguments elements) {
		super(elements);
	}

}
