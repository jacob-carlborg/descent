package descent.internal.core.dom;

import java.util.List;

public class NewDeclaration extends FuncDeclaration {
	
	public int varargs;
	private Argument[] arguments;

	public NewDeclaration(List<Argument> arguments, int varargs) {
		super(new Identifier(Id.classNew, TOK.TOKidentifier), STC.STCstatic, null);
		
		if (arguments == null) {
			this.arguments = new Argument[0];
		} else {
			this.arguments = arguments.toArray(new Argument[arguments.size()]);
		}
		
		this.varargs = varargs;
		this.ident = new Identifier("new", TOK.TOKthis);
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
		return IFunctionDeclaration.NEW;
	}

}
