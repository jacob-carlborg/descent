package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IName;
import descent.core.dom.IStatement;
import descent.core.domX.IASTVisitor;

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
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, fbody);
		}
		visitor.endVisit(this);
	}

}
