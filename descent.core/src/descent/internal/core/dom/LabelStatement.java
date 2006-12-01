package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.ILabelStatement;
import descent.core.dom.ISimpleName;
import descent.core.dom.IStatement;

public class LabelStatement extends Statement implements ILabelStatement {
	
	public Identifier ident;
	public Statement s;

	public LabelStatement(Identifier ident, Statement s) {
		this.ident = ident;
		this.s = s;
	}
	
	public ISimpleName getName() {
		return ident;
	}
	
	public IStatement getStatement() {
		return s;
	}
	
	public int getElementType() {
		return LABEL_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChild(visitor, s);
		}
		visitor.endVisit(this);
	}

}
