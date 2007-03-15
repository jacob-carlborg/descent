package descent.internal.compiler.parser;

public class DeclarationStatement extends ExpStatement {

	public DeclarationStatement(Expression exp) {
		super(exp);
	}
	
	public DeclarationStatement(Dsymbol s) {
		super(new DeclarationExp(s));
	}
	
	@Override
	public int kind() {
		return DECLARATION_STATEMENT;
	}

}
