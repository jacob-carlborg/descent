package dtool.dom.statements;

import util.tree.TreeVisitor;
import descent.internal.core.dom.LabelStatement;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit.Symbol;

public class StatementLabel extends Statement {

	public Symbol label;
	
	public StatementLabel(LabelStatement elem) {
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
