package dtool.ast.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ContinueStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.Symbol;

public class StatementContinue extends Statement {

	public Symbol id;

	public StatementContinue(ContinueStatement elem) {
		convertNode(elem);
		if(elem.ident != null)
			this.id = new Symbol(elem.ident);
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