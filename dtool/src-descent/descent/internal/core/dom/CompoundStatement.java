package descent.internal.core.dom;

import java.util.List;

import util.tree.TreeVisitor;

import descent.core.dom.ICompoundStatement;
import descent.core.dom.IStatement;
import descent.core.domX.IASTVisitor;

public class CompoundStatement extends Statement implements ICompoundStatement {

	public final List<Statement> as;

	public CompoundStatement(List<Statement> as) {
		this.as = as;
	}
	
	public IStatement[] getStatements() {
		if (as == null) return new IStatement[0];
		return this.as.toArray(new IStatement[as.size()]);
	}
	
	public int getElementType() {
		return ElementTypes.COMPOUND_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, as);
		}
		visitor.endVisit(this);
	}

}
