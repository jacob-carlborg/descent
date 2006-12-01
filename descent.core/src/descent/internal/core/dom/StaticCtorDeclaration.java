package descent.internal.core.dom;

import descent.core.dom.ISimpleName;

public class StaticCtorDeclaration extends FuncDeclaration {
	
	public StaticCtorDeclaration() {
		// TODO Auto-generated constructor stub
		super(null, 0, null);
		this.ident = new Identifier("this", TOK.TOKidentifier);
	}
	
	@Override
	public ISimpleName getName() {
		return ident;
	}
	
	@Override
	public int getFunctionDeclarationType() {
		return STATIC_CONSTRUCTOR;
	}

}
