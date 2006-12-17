package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IStringExpression;

public class StringExp extends Expression implements IStringExpression {
	
	private String s;
	private int postfix;

	public StringExp(String s, int len, int postfix) {
		this.s = s;
		this.postfix = postfix;
	}
	
	public String getEscapedValue() {
		return s;
	}
	
	public char getPostfix() {
		return (char) postfix;
	}
	
	public int getNodeType0() {
		return STRING_LITERAL;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
