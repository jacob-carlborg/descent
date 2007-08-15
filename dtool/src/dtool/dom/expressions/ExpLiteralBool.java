package dtool.dom.expressions;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.BoolExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpLiteralBool extends Expression {
	
	boolean value;

	public ExpLiteralBool(BoolExp elem) {
		convertNode(elem);
		Assert.failTODO();
		//this.value = elem.;
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	
	}

}
