package descent.internal.compiler.parser;

public class DeclarationStatement extends ExpStatement {

	public DeclarationStatement(Loc loc, Expression exp) {
		super(loc, exp);
	}
	
	public DeclarationStatement(Loc loc, Dsymbol s) {
		super(loc, new DeclarationExp(loc, s));
	}
	
	@Override
	public int getNodeType() {
		return DECLARATION_STATEMENT;
	}

}
