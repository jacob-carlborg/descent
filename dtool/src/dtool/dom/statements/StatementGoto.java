package dtool.dom.statements;

import util.tree.TreeVisitor;
import descent.internal.core.dom.GotoStatement;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DefUnit.Symbol;

public class StatementGoto extends ASTNeoNode {

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
