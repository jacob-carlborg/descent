package dtool.ast.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ThrowStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;

public class StatementThrow extends Statement {

	public Resolvable exp;

	public StatementThrow(ThrowStatement elem) {
		convertNode(elem);
		this.exp = Expression.convert(elem.exp);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}

}