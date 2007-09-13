package dtool.dom.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.CaseStatement;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;
import dtool.dom.expressions.Resolvable;

public class StatementCase extends Statement {

	public Resolvable exp;
	public IStatement st;
	
	public StatementCase(CaseStatement elem) {
		convertNode(elem);
		this.exp = Expression.convert(elem.exp);
		this.st = Statement.convert(elem.statement);
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
