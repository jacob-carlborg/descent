package dtool.dom.statements;

import util.tree.TreeVisitor;
import descent.internal.core.dom.DefaultStatement;
import dtool.dom.ast.IASTNeoVisitor;

public class StatementDefault extends Statement {

	public Statement st;
	
	public StatementDefault(DefaultStatement elem) {
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
