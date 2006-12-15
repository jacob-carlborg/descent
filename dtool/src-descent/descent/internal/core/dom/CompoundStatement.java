package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ICompoundStatement;
import descent.core.dom.IStatement;
import descent.core.domX.ASTVisitor;

public class CompoundStatement extends Statement implements ICompoundStatement {

	private final List<Statement> as;

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
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, as);
		}
		visitor.endVisit(this);
	}

}
