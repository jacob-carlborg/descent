package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IInvariantDeclaration;
import descent.core.dom.IName;
import descent.core.dom.IDescentStatement;
import descent.core.domX.IASTVisitor;

public class InvariantDeclaration extends Dsymbol implements IInvariantDeclaration {

	public Statement fbody;

	public InvariantDeclaration() {
		this.ident = new Identifier("invariant", TOK.TOKinvariant);
	}
	
	public IName getName() {
		return ident;
	}
	
	public IDescentStatement getStatement() {
		return fbody;
	}
	
	public int getElementType() {
		return ElementTypes.INVARIANT_DECLARATION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, fbody);
		}
		visitor.endVisit(this);
	}

}
