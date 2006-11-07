package descent.internal.core.dom;

import descent.core.dom.IStringExpression;

public class StringExp extends Expression implements IStringExpression {
	
	private String s;
	private int postfix;

	public StringExp(Loc loc, String s, int len, int postfix) {
		this.s = s;
		this.postfix = postfix;
	}
	
	public String getString() {
		return s;
	}
	
	public char getPostfix() {
		return (char) postfix;
	}
	
	public int getExpressionType() {
		return EXPRESSION_STRING;
	}

}
