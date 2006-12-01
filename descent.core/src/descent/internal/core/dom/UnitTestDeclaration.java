package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.ISimpleName;
import descent.core.dom.IStatement;
import descent.core.dom.IUnitTestDeclaration;

public class UnitTestDeclaration extends Dsymbol implements IUnitTestDeclaration {

	public Statement fbody;

	public UnitTestDeclaration() {
		this.ident = new Identifier("unittest", TOK.TOKidentifier);
	}
	
	public ISimpleName getName() {
		return ident;
	}
	
	public IStatement getStatement() {
		return fbody;
	}
	
	public int getElementType() {
		return UNITTEST_DECLARATION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, fbody);
		}
		visitor.endVisit(this);
	}

}
