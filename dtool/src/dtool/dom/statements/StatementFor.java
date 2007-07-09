package dtool.dom.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.ForStatement;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;

public class StatementFor extends Statement {

	public Statement init;
	public Expression cond;
	public Expression inc;
	public Statement body;


	public StatementFor(ForStatement elem) {
		convertNode(elem);
		this.init = Statement.convert(elem.init);
		this.cond = Expression.convert(elem.condition);
		this.inc = Expression.convert(elem.increment);
		this.body = Statement.convert(elem.body);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, init);
			TreeVisitor.acceptChildren(visitor, cond);
			TreeVisitor.acceptChildren(visitor, inc);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}
