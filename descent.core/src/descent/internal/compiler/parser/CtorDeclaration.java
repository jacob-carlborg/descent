package descent.internal.compiler.parser;

import java.util.List;

public class CtorDeclaration extends FuncDeclaration {
	
	public List<Argument> arguments;
	public boolean varargs;
	
	public CtorDeclaration(List<Argument> arguments, boolean varags) {
		this.arguments = arguments;
		this.varargs = varags;
	}
	
	@Override
	public int kind() {
		return CTOR_DECLARATION;
	}

}
