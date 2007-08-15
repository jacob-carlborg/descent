package dtool.dom.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ReturnStatement;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;

public class StatementReturn extends Statement {

	public Expression exp;

	public StatementReturn(ReturnStatement element) {
		convertNode(element);
		this.exp = Expression.convert(element.exp);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, exp);
		}
		visitor.endVisit(this);	 
	}

}
