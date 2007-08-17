package dtool.dom.expressions;

import descent.internal.compiler.parser.IntegerExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpLiteralBool extends Expression {
	
	public final boolean value;

	public ExpLiteralBool(IntegerExp node) {
		convertNode(node);
		this.value = node.value.intValue() != 0;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	
	}

}
