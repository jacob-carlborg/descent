package dtool.dom.statements;

import util.tree.TreeVisitor;
import descent.internal.core.dom.VolatileStatement;
import dtool.dom.ast.IASTNeoVisitor;

public class StatementVolatile extends Statement {
	
	public Statement st;

	public StatementVolatile(VolatileStatement elem) {
		convertNode(elem);
		this.st = Statement.convert(elem.s);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, st);
		}
		visitor.endVisit(this);
	}

}
