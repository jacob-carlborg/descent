package descent.internal.compiler.parser;

public class Modifier extends ASTNode {
	
	public TOK tok;

	public Modifier(TOK tok) {
		this.tok = tok;
	}
	
	@Override
	public String toString() {
		return tok.toString();
	}
	
	@Override
	public int getNodeType() {
		return MODIFIER;
	}

}
