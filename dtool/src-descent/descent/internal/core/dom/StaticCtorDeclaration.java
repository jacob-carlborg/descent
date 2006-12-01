package descent.internal.core.dom;

import descent.core.dom.IName;

public class StaticCtorDeclaration extends FuncDeclaration {
	
	public StaticCtorDeclaration() {
		// TODO Auto-generated constructor stub
		super(null, 0, null);
		this.ident = new Identifier("this", TOK.TOKidentifier);
	}
	
	@Override
	public IName getName() {
		return ident;
	}
	
	@Override
	public int getFunctionDeclarationType() {
		return STATIC_CONSTRUCTOR;
	}

}