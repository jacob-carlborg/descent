package descent.internal.core.dom;


public class StaticDtorDeclaration extends FuncDeclaration {
	
	public StaticDtorDeclaration() {
		// TODO Auto-generated constructor stub
		super(null, 0, null);
		this.ident = new Identifier("~this", TOK.TOKidentifier);
	}
	
	@Override
	public Identifier getName() {
		return ident;
	}
	
	@Override
	public int getFunctionDeclarationType() {
		return STATIC_DESTRUCTOR;
	}

}
