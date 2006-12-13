package descent.internal.core.dom;

import descent.core.dom.IOnScopeStatement;
import descent.core.dom.IStatement;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class OnScopeStatement extends Statement implements IOnScopeStatement  {

	private final TOK t2;
	private final Statement st;

	public OnScopeStatement(TOK t2, Statement st) {
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
	
	public int getElementType() {
		return ElementTypes.ON_SCOPE_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, st);
		}
		visitor.endVisit(this);
	}

}
