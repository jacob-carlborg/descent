package dtool.dom.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.GotoStatement;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.Symbol;

public class StatementGoto extends Statement {

	public Symbol label;
	
	public StatementGoto(GotoStatement elem) {
		convertNode(elem);
		this.label = new Symbol(elem.ident);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, label);
		}
		visitor.endVisit(this);
	}

}
