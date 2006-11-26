package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IName;
import descent.core.dom.IStatement;
import descent.core.dom.IUnitTestDeclaration;

public class UnitTestDeclaration extends Dsymbol implements IUnitTestDeclaration {

	public Statement fbody;

	public UnitTestDeclaration(Loc loc, Loc loc2) {
		this.ident = new Identifier("unittest", TOK.TOKidentifier);
	}
	
	public IName getName() {
		return ident;
	}
	
	public IStatement getStatement() {
		return fbody;
	}
	
	public int getElementType() {
		return UNITTEST_DECLARATION;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, fbody);
		}
		visitor.endVisit(this);
	}

}
