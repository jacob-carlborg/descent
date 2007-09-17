package dtool.ast.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.VolatileStatement;
import dtool.ast.IASTNeoVisitor;

public class StatementVolatile extends Statement {
	
	public IStatement st;

	public StatementVolatile(VolatileStatement elem) {
		convertNode(elem);
		this.st = Statement.convert(elem.statement);
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
