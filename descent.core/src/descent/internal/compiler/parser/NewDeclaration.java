package descent.internal.compiler.parser;

import java.util.List;

public class NewDeclaration extends FuncDeclaration {
	
	public List<Argument> arguments;
	public int varargs;
	
	public NewDeclaration(List<Argument> arguments, int varags) {
		this.arguments = arguments;
		this.varargs = varags;
	}
	
	@Override
	public int kind() {
		return NEW_DECLARATION;
	}

}
