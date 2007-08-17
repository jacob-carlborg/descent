package dtool.dom.expressions;

import java.math.BigInteger;

import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.TY;
import descent.internal.compiler.parser.TypeBasic;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpLiteralInteger extends Expression {
	
	BigInteger num;

	public ExpLiteralInteger(IntegerExp elem) {
		convertNode(elem);
		num = elem.value;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}

	public static ASTNeoNode convertIntegerExp(IntegerExp node) {
		if(((TypeBasic) node.type).ty == TY.Tbool)
			return new ExpLiteralBool(node);
		else
			return new ExpLiteralInteger(node);
			
	}

}
