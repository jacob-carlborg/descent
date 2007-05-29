package dtool.dom.statements;

import util.tree.TreeVisitor;
import descent.internal.core.dom.CaseStatement;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;

public class StatementCase extends ASTNeoNode {

	public Expression exp;
	public Statement st;
	
	public StatementCase(CaseStatement elem) {
		convertNode(elem);
		this.exp = Expression.convert(elem.exp);
		this.st = Statement.convert(elem.s);
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
