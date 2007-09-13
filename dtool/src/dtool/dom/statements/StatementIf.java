package dtool.dom.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.IfStatement;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;
import dtool.dom.expressions.Resolvable;

public class StatementIf extends Statement {

	public Resolvable pred;
	public IStatement thenbody;
	public IStatement elsebody;

	public StatementIf(IfStatement elem) {
		convertNode(elem);
		this.pred = Expression.convert(elem.condition);
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
