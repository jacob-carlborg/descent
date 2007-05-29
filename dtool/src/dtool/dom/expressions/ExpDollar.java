package dtool.dom.expressions;

import descent.internal.core.dom.DollarExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpDollar extends Expression {

	public ExpDollar(DollarExp elem) {
		convertNode(elem);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
