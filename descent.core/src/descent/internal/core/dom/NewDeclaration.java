package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IArgument;

public class NewDeclaration extends FuncDeclaration {
	
	public int varargs;
	private IArgument[] arguments;

	public NewDeclaration(Loc loc, int endloc, List<Argument> arguments, int varargs) {
		super(loc, endloc, new Identifier(Id.classNew, TOK.TOKidentifier), STC.STCstatic, null);
		
		if (arguments == null) {
			this.arguments = new IArgument[0];
		} else {
			this.arguments = arguments.toArray(new IArgument[arguments.size()]);
		}
		
		this.varargs = varargs;
		this.ident = new Identifier("new", TOK.TOKthis);
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
		return NEW;
	}

}
