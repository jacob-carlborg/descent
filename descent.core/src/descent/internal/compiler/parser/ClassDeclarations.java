package descent.internal.compiler.parser;

// DMD 1.020
public class ClassDeclarations extends Array<IClassDeclaration> {

	private static final long serialVersionUID = 1L;
	
	public ClassDeclarations() {
	}
	
	public ClassDeclarations(int capacity) {
		super(capacity);
	}
	
	public ClassDeclarations(ClassDeclarations elements) {
		super(elements);
	}

}
