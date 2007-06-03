package dtool.dom.statements;

import util.tree.TreeVisitor;
import descent.internal.core.dom.IfStatement;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;

public class StatementIf extends Statement {

	public Expression pred;
	public Statement thenbody;
	public Statement elsebody;

	public StatementIf(IfStatement elem) {
		convertNode(elem);
		this.pred = Expression.convert(elem.expr);
		this.thenbody = Statement.convert(elem.ifbody);
		this.elsebody = Statement.convert(elem.elsebody);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, pred);
			TreeVisitor.acceptChildren(visitor, thenbody);
			TreeVisitor.acceptChildren(visitor, elsebody);
		}
		visitor.endVisit(this);
	}

}
