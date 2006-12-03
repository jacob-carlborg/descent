package descent.internal.core.dom;

import descent.core.dom.IDefaultStatement;
import descent.core.dom.ASTVisitor;
import descent.core.dom.IStatement;

public class DefaultStatement extends Statement implements IDefaultStatement {

	private final Statement s;

	public DefaultStatement(Statement s) {
		this.s = s;
	}
	
	public int getNodeType0() {
		return DEFAULT_STATEMENT;
	}
	
	public IStatement getStatement() {
		return s;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, s);
		}
		visitor.endVisit(this);
	}

}
