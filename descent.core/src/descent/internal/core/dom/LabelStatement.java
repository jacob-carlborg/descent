package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.ILabelStatement;
import descent.core.dom.IName;
import descent.core.dom.IStatement;

public class LabelStatement extends Statement implements ILabelStatement {
	
	public Identifier ident;
	public Statement s;

	public LabelStatement(Loc loc, Identifier ident, Statement s) {
		this.ident = ident;
		this.s = s;
	}
	
	public IName getName() {
		return ident;
	}
	
	public IStatement getStatement() {
		return s;
	}
	
	public int getStatementType() {
		return STATEMENT_LABEL;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChild(visitor, s);
		}
		visitor.endVisit(this);
	}

}
