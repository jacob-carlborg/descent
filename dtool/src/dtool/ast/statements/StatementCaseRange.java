package dtool.ast.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.CaseRangeStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;

public class StatementCaseRange extends Statement {

	public Resolvable expFirst;
	public Resolvable expLast;
	public IStatement st;
	
	public StatementCaseRange(CaseRangeStatement elem) {
		convertNode(elem);
		this.expFirst = Expression.convert(elem.first);
		this.expLast = Expression.convert(elem.last);
		this.st = Statement.convert(elem.statement);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, expFirst);
			TreeVisitor.acceptChildren(visitor, expLast);
			TreeVisitor.acceptChildren(visitor, st);
		}
		visitor.endVisit(this);
	}

}
