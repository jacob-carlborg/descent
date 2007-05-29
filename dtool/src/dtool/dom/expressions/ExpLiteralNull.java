package dtool.dom.expressions;

import descent.internal.core.dom.NullExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpLiteralNull extends Expression {

	public ExpLiteralNull(NullExp elem) {
		convertNode(elem);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
