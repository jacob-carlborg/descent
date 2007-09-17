package dtool.ast.references;

import melnorme.miscutil.Assert;
import dtool.ast.IASTNeoVisitor;

public class RefIdentifier extends CommonRefSingle {

	public RefIdentifier() { super(); }

	public RefIdentifier(String name) {
		Assert.isTrue(name != null);
		this.name = name;
	}
	
	public RefIdentifier(descent.internal.compiler.parser.IdentifierExp elem) {
		setSourceRange(elem);
		Assert.isTrue(!(elem.ident.length == 0));
		this.name = new String(elem.ident);
	}

	public RefIdentifier(descent.internal.compiler.parser.TypeBasic elem) {
		setSourceRange(elem);
		Assert.isNotNull(elem.ty.name);
		this.name = elem.toString();
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return name;
	}
}

