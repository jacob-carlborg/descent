package dtool.dom.expressions;

import java.math.BigInteger;

import descent.internal.compiler.parser.IntegerExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpLiteralInteger extends Expression {
	
	BigInteger num;

	public ExpLiteralInteger(IntegerExp elem) {
		convertNode(elem);
		num = elem.value;
		//elem.str;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}

}
