package descent.internal.core.dom;

import descent.core.dom.IDefaultStatement;
import descent.core.dom.IStatement;
import descent.core.domX.ASTVisitor;

public class DefaultStatement extends Statement implements IDefaultStatement {

	private final Statement s;

	public DefaultStatement(Statement s) {
		this.s = s;
	}
	
	public int getElementType() {
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