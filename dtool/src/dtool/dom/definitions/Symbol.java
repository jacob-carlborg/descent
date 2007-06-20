package dtool.dom.definitions;

import util.Assert;
import descent.internal.core.dom.Identifier;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;

/** A Symbol is node wrapping an identifier, used only in DefUnits names.*/
public class Symbol extends ASTNeoNode {
	public String name;

	public Symbol(Identifier id) {
		Assert.isTrue(id.getClass() == Identifier.class);
		setSourceRange(id);
		this.name = id.string;
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