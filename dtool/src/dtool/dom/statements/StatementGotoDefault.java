package dtool.dom.statements;

import descent.internal.compiler.parser.GotoDefaultStatement;
import dtool.dom.ast.IASTNeoVisitor;

public class StatementGotoDefault extends Statement {

	public StatementGotoDefault(GotoDefaultStatement node) {
		convertNode(node);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}

}
