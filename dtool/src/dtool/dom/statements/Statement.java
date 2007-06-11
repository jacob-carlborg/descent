package dtool.dom.statements;

import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;

public abstract class Statement extends ASTNeoNode implements IStatement {

	public static Statement convert(descent.internal.core.dom.Statement elem) {
		return (Statement) DescentASTConverter.convertElem(elem);
	}

}
