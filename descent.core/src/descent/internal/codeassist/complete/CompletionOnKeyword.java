package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class CompletionOnKeyword extends ASTDmdNode implements ICompletionOnKeyword {
	
	private final char[] token;
	private final char[][] possibleKeywords;
	
	public CompletionOnKeyword(char[] token, char[][] toks) {
		this.token = token;
		this.possibleKeywords = toks;
	}

	@Override
	public int getNodeType() {
		return -1;
	}

	@Override
	protected void accept0(IASTVisitor visitor) {
		throw new IllegalStateException("Illegal to visit this node");
	}

	public boolean canCompleteEmptyToken() {
		return true;
	}

	public char[][] getPossibleKeywords() {
		return possibleKeywords;
	}

	public char[] getToken() {
		return token;
	}

}
