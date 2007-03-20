package descent.internal.compiler.parser;

public class DeclarationExp extends Expression {
	
	public Dsymbol declaration;
	
	public DeclarationExp(Dsymbol declaration) {
		super(TOK.TOKdeclaration);
		this.declaration =  declaration;
	}
	
	@Override
	public int getNodeType() {
		return DECLARATION_EXP;
	}

}
