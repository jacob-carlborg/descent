package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IArgument;

public class DeleteDeclaration extends FuncDeclaration {
	
	private IArgument[] arguments;

	public DeleteDeclaration(List<Argument> arguments) {
		super(new Identifier(Id.classDelete, TOK.TOKidentifier), STC.STCstatic, null);
		
		if (arguments == null) {
			this.arguments = new IArgument[0];
		} else {
			this.arguments = arguments.toArray(new IArgument[arguments.size()]);
		}
		
		this.ident = new Identifier("delete", TOK.TOKthis);
	}
	
	public IArgument[] getArguments() {
		return arguments;
	}
	
	@Override
	public boolean isVariadic() {
		return false;
	}
	
	@Override
	public int getFunctionDeclarationType() {
		return DELETE;
	}
	
}
