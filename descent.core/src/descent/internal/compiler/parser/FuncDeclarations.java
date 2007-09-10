package descent.internal.compiler.parser;

import java.util.ArrayList;

public class FuncDeclarations extends ArrayList<FuncDeclaration> {

	private static final long serialVersionUID = 1L;
	
	public FuncDeclarations() {
	}
	
	public FuncDeclarations(int capacity) {
		super(capacity);
	}
	
	public FuncDeclarations(FuncDeclarations elements) {
		super(elements);
	}

}
