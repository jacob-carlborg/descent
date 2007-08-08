package descent.internal.compiler.parser;

public class Modifier extends ASTNode {
	
	public TOK tok;

	public Modifier(Token token) {
		this.tok = token.value;
		this.start = token.ptr;
		this.length = token.len;
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
