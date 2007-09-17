package dtool.ast.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.SynchronizedStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;

public class StatementSynchronized extends Statement {

	public Resolvable exp;
	public IStatement body;

	public StatementSynchronized(SynchronizedStatement elem) {
		convertNode(elem);
		this.exp = Expression.convert(elem.exp);
		this.body = Statement.convert(elem.body);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}
