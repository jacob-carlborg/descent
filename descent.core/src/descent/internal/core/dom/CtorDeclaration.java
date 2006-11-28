package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IArgument;

public class CtorDeclaration extends FuncDeclaration {
	
	public int varargs;
	private IArgument[] arguments;

	public CtorDeclaration(List<Argument> arguments, int varargs) {
		super(new Identifier(Id.ctor, TOK.TOKidentifier), STC.STCundefined, null);
		
		this.arguments = arguments.toArray(new IArgument[arguments.size()]);
		
		this.varargs = varargs;
		this.ident = new Identifier("this", TOK.TOKthis);
	}
	
	public IArgument[] getArguments() {
		return arguments;
	}
	
	@Override
	public boolean isVariadic() {
		return varargs != 0;
	}
	
	@Override
	public int getFunctionDeclarationType() {
		return CONSTRUCTOR;
	}

}
