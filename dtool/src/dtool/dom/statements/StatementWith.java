package dtool.dom.statements;

import util.tree.TreeVisitor;
import descent.internal.core.dom.WithStatement;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;

public class StatementWith extends ASTNeoNode {

	public Expression exp;
	public Statement body;

	public StatementWith(WithStatement elem) {
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
