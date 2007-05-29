package dtool.dom.expressions;

import util.Assert;
import descent.internal.core.dom.IntegerExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpLiteralInteger extends Expression {
	
	long num;

	public ExpLiteralInteger(IntegerExp elem) {
		Assert.isTrue(elem.expressionType == ElementTypes.INTEGER_EXPRESSION);
		convertNode(elem);
		num = elem.number.longValue();
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}

}
