package descent.internal.compiler.parser;

import java.util.List;

public class TypeFunction extends Type {
	
	public List<Argument> arguments;
	public boolean varargs;

	public TypeFunction(List<Argument> arguments, Type treturn, boolean varargs) {
		super(TY.Tfunction, treturn);
		this.arguments = arguments;
		this.varargs = varargs;
	}
	
	@Override
	public int kind() {
		return TYPE_FUNCTION;
	}

}
