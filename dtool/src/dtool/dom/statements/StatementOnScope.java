package dtool.dom.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.OnScopeStatement;
import dtool.dom.ast.IASTNeoVisitor;

public class StatementOnScope extends Statement {
	
	public Statement st;

	public StatementOnScope(OnScopeStatement elem) {
		convertNode(elem);
		this.st = Statement.convert(elem.st);
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
