package descent.internal.core.dom;

import descent.core.dom.IName;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;

public class Identifier extends AbstractElement implements IName {
	
	public String string;
	public TOK value;
	
	public Identifier(String string, TOK value) {
		this.string = string;
		this.value = value;
	}
	
	public Identifier(Token token) {
		this.string = token.ident.string;
		this.value = token.value;
		this.startPos = token.ptr;
		this.length = token.len;
	}
	
	@Override
	public String toString() {
		return string;
	}

	public void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	public int getElementType() {
		return ElementTypes.NAME;
	}

}
