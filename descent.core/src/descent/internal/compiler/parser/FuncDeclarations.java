package descent.internal.compiler.parser;

// DMD 1.020
public class FuncDeclarations extends Array<IFuncDeclaration> {

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
