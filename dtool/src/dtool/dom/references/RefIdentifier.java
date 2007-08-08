package dtool.dom.references;

import dtool.dom.ast.IASTNeoVisitor;

public class RefIdentifier extends CommonRefSingle {

	public RefIdentifier() { super(); }

	public RefIdentifier(String name) {
		this.name = name;
	}
	
	public RefIdentifier(descent.internal.core.dom.Identifier elem) {
		setSourceRange(elem);
		this.name = elem.string;
	}

	public RefIdentifier(descent.internal.core.dom.TypeBasic elem) {
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

