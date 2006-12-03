package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IComment;

public class Comment extends ASTNode implements IComment  {
	
	public TOK tok;
	public String string;
	
	@Override
	void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public int getElementType() {
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
