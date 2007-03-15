package descent.internal.compiler.parser;

import java.util.List;

public class DeleteDeclaration extends FuncDeclaration {
	
	public List<Argument> arguments;
	public int varargs;
	
	public DeleteDeclaration(List<Argument> arguments, int varags) {
		this.arguments = arguments;
		this.varargs = varags;
	}
	
	@Override
	public int kind() {
		return DELETE_DECLARATION;
	}

}
