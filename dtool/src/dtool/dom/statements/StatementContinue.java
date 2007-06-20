package dtool.dom.statements;

import util.tree.TreeVisitor;
import descent.internal.core.dom.ContinueStatement;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.Symbol;

public class StatementContinue extends Statement {

	public Symbol id;

	public StatementContinue(ContinueStatement elem) {
		convertNode(elem);
		this.id = new Symbol(elem.id);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, id);
		}
		visitor.endVisit(this);
	}

}
