package dtool.dom.base;

import dtool.dom.ast.IASTNeoVisitor;

public class EntIdentifier extends EntitySingle {

	public EntIdentifier() { super(); }

	public EntIdentifier(String name) {
		this.name = name;
	}
	
	public EntIdentifier(descent.internal.core.dom.Identifier elem) {
		setSourceRange(elem);
		this.name = elem.string;
	}

	public EntIdentifier(descent.internal.core.dom.TypeBasic elem) {
		setSourceRange(elem);
		this.name = elem.toString();
	}

	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public String toString() {
		return name;
	}
}

