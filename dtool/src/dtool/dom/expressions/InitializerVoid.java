package dtool.dom.expressions;

import descent.internal.core.dom.VoidInitializer;
import dtool.dom.ast.IASTNeoVisitor;

public class InitializerVoid extends Initializer {

	public InitializerVoid(VoidInitializer elem) {
		convertNode(elem);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);

	}

}
