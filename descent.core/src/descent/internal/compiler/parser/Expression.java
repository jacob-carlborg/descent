package descent.internal.compiler.parser;

public abstract class Expression extends ASTNode {
	
	public TOK op;
	public Type type;
	
	public Expression(TOK op) {
		this.op = op;
	}

}
