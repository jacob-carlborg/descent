package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class Modifier extends ASTDmdNode {
	
	public int lineNumber;
	public TOK tok;

	public Modifier(Token token, int lineNumber) {
		this.tok = token.value;
		this.start = token.ptr;
		this.length = token.sourceLen;
		this.lineNumber = lineNumber;
	}
	
	@Override
	public String toString() {
		return tok.toString();
	}
	
	@Override
	public int getNodeType() {
		return MODIFIER;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public char[] toCharArray() {
		return tok.charArrayValue;
	}
	
	@Override
	public int getLineNumber() {
		return lineNumber;
	}

}
