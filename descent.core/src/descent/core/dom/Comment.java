package descent.core.dom;

import descent.internal.core.parser.TOK;

public class Comment extends ASTNode {
	
	public TOK tok;
	public String string;
	
	public Comment(AST ast) {
		super(ast);
	}
	
	@Override
	void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public int getNodeType0() {
		return COMMENT;
	}
	
	public String getComment() {
		return string;
	}
	
	public boolean isDocComment() {
		return tok == TOK.TOKdocblockcomment ||
			tok == TOK.TOKdoclinecomment ||
			tok == TOK.TOKdocpluscomment;
	}

}
