package dtool.dom.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.DoStatement;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;

public class StatementDo extends Statement {

	public Expression exp;
	public Statement st;

	public StatementDo(DoStatement elem) {
		convertNode(elem);
		this.exp = Expression.convert(elem.expr);
		this.st = Statement.convert(elem.body);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, st);
		}
		visitor.endVisit(this);
	}

}
