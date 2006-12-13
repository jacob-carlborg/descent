package descent.internal.core.dom;

import descent.core.dom.ILabelStatement;
import descent.core.dom.IName;
import descent.core.dom.IStatement;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class LabelStatement extends Statement implements ILabelStatement {
	
	public Identifier ident;
	public Statement s;

	public LabelStatement(Identifier ident, Statement s) {
		this.ident = ident;
		this.s = s;
	}
	
	public IName getName() {
		return ident;
	}
	
	public IStatement getStatement() {
		return s;
	}
	
	public int getElementType() {
		return ElementTypes.LABEL_STATEMENT;
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
