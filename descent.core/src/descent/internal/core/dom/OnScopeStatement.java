package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IOnScopeStatement;
import descent.core.dom.IStatement;

public class OnScopeStatement extends Statement implements IOnScopeStatement  {

	private final TOK t2;
	private final Statement st;

	public OnScopeStatement(Loc loc, TOK t2, Statement st) {
		this.t2 = t2;
		this.st = st;
	}
	
	public IStatement getStatement() {
		return st;
	}
	
	public int getOnScopeType() {
		switch(t2) {
		case TOKon_scope_exit: return ON_SCOPE_EXIT;
		case TOKon_scope_failure: return ON_SCOPE_FAILURE;
		case TOKon_scope_success: return ON_SCOPE_SUCCESS;
		}
		throw new IllegalStateException("Can't happen");
	}
	
	public int getStatementType() {
		return STATEMENT_ON_SCOPE;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, st);
		}
		visitor.endVisit(this);
	}

}
