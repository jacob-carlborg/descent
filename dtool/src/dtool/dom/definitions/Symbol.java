package dtool.dom.definitions;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.IdentifierExp;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;

/** A Symbol is node wrapping an identifier, used only in DefUnits names.*/
public class Symbol extends ASTNeoNode {
	public String name;

	public Symbol(IdentifierExp id) {
		Assert.isTrue(id.getClass() == IdentifierExp.class);
		setSourceRange(id);
		this.name = id.ident;
	}

	public Symbol(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		return this.name.equals(obj);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public String toString() {
		return name;
	}
}