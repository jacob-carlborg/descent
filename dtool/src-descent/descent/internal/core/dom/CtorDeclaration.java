package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IArgument;

public class CtorDeclaration extends FuncDeclaration {
	
	public int varargs;
	private Argument[] arguments;

	public CtorDeclaration(List<Argument> arguments, int varargs) {
		super(new Identifier(Id.ctor, TOK.TOKidentifier), STC.STCundefined, null);
		
		this.arguments = arguments.toArray(new Argument[arguments.size()]);
		
		this.varargs = varargs;
		this.ident = new Identifier("this", TOK.TOKthis);
	}
	
	public Argument[] getArguments() {
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
