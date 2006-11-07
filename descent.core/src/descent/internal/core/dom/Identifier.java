package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IName;

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
		this.start = token.ptr;
		this.length = token.len;
	}
	
	@Override
	public String toString() {
		return string;
	}

	public void accept(IDElementVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	public int getElementType() {
		return NAME;
	}

}
