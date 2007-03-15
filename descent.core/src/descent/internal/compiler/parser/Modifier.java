package descent.internal.compiler.parser;

public class Modifier extends ASTNode {
	
	public TOK tok;

	public Modifier(TOK tok) {
		this.tok = tok;
	}
	
	@Override
	public int kind() {
		return MODIFIER;
	}

}
