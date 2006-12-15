package descent.internal.core.dom;



public class DtorDeclaration extends FuncDeclaration {
	
	public DtorDeclaration() {
		super(new Identifier(Id.dtor, TOK.TOKidentifier), STC.STCundefined, null);
		this.ident = new Identifier("~this", TOK.TOKidentifier);
	}
	
	@Override
	public int getFunctionDeclarationType() {
		return IFunctionDeclaration.DESTRUCTOR;
	}

}
