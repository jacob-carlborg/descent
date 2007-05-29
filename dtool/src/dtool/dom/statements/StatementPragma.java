package dtool.dom.statements;

import util.tree.TreeVisitor;
import descent.internal.core.dom.PragmaStatement;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DefUnit.Symbol;
import dtool.dom.expressions.Expression;

public class StatementPragma extends ASTNeoNode {

	public Symbol ident;
	public Expression[] exps;
	public Statement body;

	public StatementPragma(PragmaStatement elem) {
		convertNode(elem);
		this.ident = new Symbol(elem.ident);
		this.exps = Expression.convertMany(elem.expressions);
		this.body = Statement.convert(elem.body);
}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, exps);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}
