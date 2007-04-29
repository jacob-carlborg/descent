package dtool.dom.expressions;

import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;

public abstract class Expression extends ASTNeoNode {

	public static Expression convert(descent.internal.core.dom.Expression exp) {
		return (Expression) DescentASTConverter.convertElem(exp);
	}

	public static Expression[] convertMany(descent.internal.core.dom.Expression[] elements) {
		Expression[] rets = new Expression[elements.length];
		DescentASTConverter.convertMany(rets, elements);
		return rets;
	}
	
}
