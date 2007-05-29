package dtool.dom.expressions;

import descent.internal.core.dom.StringExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpLiteralString extends Expression {

	public ExpLiteralString(StringExp elem) {
		convertNode(elem);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}

}
