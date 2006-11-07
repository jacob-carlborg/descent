package descent.internal.core.dom;


public class DtorDeclaration extends FuncDeclaration {
	
	public DtorDeclaration(Loc loc, int i) {
		super(loc, i, new Identifier(Id.dtor, TOK.TOKidentifier), STC.STCundefined, null);
		this.ident = new Identifier("~this", TOK.TOKidentifier);
	}
	
	@Override
	public int getFunctionDeclarationType() {
		return DESTRUCTOR;
	}

}
