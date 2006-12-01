package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.ISimpleName;

public class Identifier extends ASTNode implements ISimpleName {
	
	public String string;
	public TOK value;
	
	public Identifier(String string, TOK value) {
		this.string = string;
		this.value = value;
	}
	
	public Identifier(Token token) {
		this.string = token.ident.string;
		this.value = token.value;
		this.startPosition = token.ptr;
		this.length = token.len;
	}
	
	public String getIdentifier() {
		return string;
	}

	public void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	public int getElementType() {
		return SIMPLE_NAME;
	}
	
	// TODO Descent remove
	@Override
	public String toString() {
		return string;
	}

}
