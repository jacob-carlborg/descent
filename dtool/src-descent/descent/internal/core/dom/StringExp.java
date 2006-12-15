package descent.internal.core.dom;

import descent.core.dom.IStringExpression;
import descent.core.domX.ASTVisitor;

public class StringExp extends Expression implements IStringExpression {
	
	private String s;
	private int postfix;

	public StringExp(String s, int len, int postfix) {
		this.s = s;
		this.postfix = postfix;
	}
	
	public String getString() {
		return s;
	}
	
	public char getPostfix() {
		return (char) postfix;
	}
	
	public int getElementType() {
		return ElementTypes.STRING_EXPRESSION;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
