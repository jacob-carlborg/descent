package dtool.dom.statements;

import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;

public abstract class Statement extends ASTNeoNode implements IStatement {

	public static IStatement convert(descent.internal.core.dom.Statement elem) {
		return (IStatement) DescentASTConverter.convertElem(elem);
	}

}
