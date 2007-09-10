package descent.internal.compiler.parser;

import java.util.ArrayList;

public class ClassDeclarations extends ArrayList<ClassDeclaration> {

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
