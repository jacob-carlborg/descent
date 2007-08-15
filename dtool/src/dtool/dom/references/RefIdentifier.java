package dtool.dom.references;

import melnorme.miscutil.Assert;
import dtool.dom.ast.IASTNeoVisitor;

public class RefIdentifier extends CommonRefSingle {

	public RefIdentifier() { super(); }

	public RefIdentifier(String name) {
		this.name = name;
	}
	
	public RefIdentifier(descent.internal.compiler.parser.IdentifierExp elem) {
		setSourceRange(elem);
		Assert.isTrue(!elem.ident.equals(""));
		this.name = elem.ident;
	}

	public RefIdentifier(descent.internal.compiler.parser.TypeBasic elem) {
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

