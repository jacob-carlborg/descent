package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IInvariantDeclaration;
import descent.core.dom.ISimpleName;
import descent.core.dom.IStatement;

public class InvariantDeclaration extends Dsymbol implements IInvariantDeclaration {

	public Statement fbody;

	public InvariantDeclaration() {
		this.ident = new Identifier("invariant", TOK.TOKinvariant);
	}
	
	public ISimpleName getName() {
		return ident;
	}
	
	public IStatement getStatement() {
		return fbody;
	}
	
	public int getNodeType0() {
		return INVARIANT_DECLARATION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, fbody);
		}
		visitor.endVisit(this);
	}

}
