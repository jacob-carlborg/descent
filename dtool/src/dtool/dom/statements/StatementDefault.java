package dtool.dom.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.DefaultStatement;
import dtool.dom.ast.IASTNeoVisitor;

public class StatementDefault extends Statement {

	public IStatement st;
	
	public StatementDefault(DefaultStatement elem) {
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
