package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.ILabelStatement;
import descent.core.dom.IName;
import descent.core.dom.IStatement;
import descent.core.domX.IASTVisitor;

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
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, ident);
			TreeVisitor.acceptChild(visitor, s);
		}
		visitor.endVisit(this);
	}

}
