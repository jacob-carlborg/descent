package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IArgument;

public class DeleteDeclaration extends FuncDeclaration {
	
	private Argument[] arguments;

	public DeleteDeclaration(List<Argument> arguments) {
		super(new Identifier(Id.classDelete, TOK.TOKidentifier), STC.STCstatic, null);
		
		if (arguments == null) {
			this.arguments = new Argument[0];
		} else {
			this.arguments = arguments.toArray(new Argument[arguments.size()]);
		}
		
		this.ident = new Identifier("delete", TOK.TOKthis);
	}
	
	public Argument[] getArguments() {
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
