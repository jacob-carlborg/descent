package descent.internal.compiler.parser;

import java.util.ArrayList;

public class Objects extends ArrayList<ASTDmdNode> {

	private static final long serialVersionUID = 1L;
	
	public Objects() {
	}
	
	public Objects(int capacity) {
		super(capacity);
	}
	
	public Objects(Objects elements) {
		super(elements);
	}

}
