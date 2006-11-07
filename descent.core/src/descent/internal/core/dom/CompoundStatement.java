package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ICompoundStatement;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IStatement;

public class CompoundStatement extends Statement implements ICompoundStatement {

	private final List<Statement> as;

	public CompoundStatement(Loc loc, List<Statement> as) {
		this.as = as;
	}
	
	public IStatement[] getStatements() {
		if (as == null) return new IStatement[0];
		return this.as.toArray(new IStatement[as.size()]);
	}
	
	public int getStatementType() {
		return STATEMENT_COMPOUND;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, as);
		}
		visitor.endVisit(this);
	}

}
