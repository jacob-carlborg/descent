package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IInvariantDeclaration;
import descent.core.dom.IName;
import descent.core.dom.IStatement;

public class InvariantDeclaration extends Dsymbol implements IInvariantDeclaration {

	public Statement fbody;

	public InvariantDeclaration(Loc loc, int i) {
		this.ident = new Identifier("invariant", TOK.TOKinvariant);
	}
	
	public IName getName() {
		return ident;
	}
	
	public IStatement getStatement() {
		return fbody;
	}
	
	public int getElementType() {
		return INVARIANT_DECLARATION;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, fbody);
		}
		visitor.endVisit(this);
	}

}
