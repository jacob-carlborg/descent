package descent.internal.core.dom;

import descent.core.dom.IName;
import descent.core.dom.IStatement;
import descent.core.domX.ASTVisitor;

public class UnitTestDeclaration extends Dsymbol {

	public Statement fbody;

	public UnitTestDeclaration() {
		this.ident = new Identifier("unittest", TOK.TOKidentifier);
	}
	
	public IName getName() {
		return ident;
	}
	
	public IStatement getStatement() {
		return fbody;
	}
	
	public int getElementType() {
		return ElementTypes.UNITTEST_DECLARATION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, fbody);
		}
		visitor.endVisit(this);
	}

}
