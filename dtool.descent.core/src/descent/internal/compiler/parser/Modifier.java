package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class Modifier extends ASTDmdNode {
	
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
	
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public char[] toCharArray() {
		return tok.charArrayValue;
	}

}
