package dtool.dom.expressions;

import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;

public abstract class Initializer extends ASTNeoNode{

	public static Initializer convert(descent.internal.core.dom.Initializer initializer) {
		return (Initializer) DescentASTConverter.convertElem(initializer);
	}

}
