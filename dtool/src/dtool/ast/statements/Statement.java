package dtool.ast.statements;

import dtool.ast.ASTNeoNode;
import dtool.descentadapter.DescentASTConverter;

public abstract class Statement extends ASTNeoNode implements IStatement {

	public static IStatement convert(descent.internal.compiler.parser.Statement elem) {
		return (IStatement) DescentASTConverter.convertElem(elem);
	}

}
