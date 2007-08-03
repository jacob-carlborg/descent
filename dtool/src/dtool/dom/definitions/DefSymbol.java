package dtool.dom.definitions;

import melnorme.miscutil.Assert;
import descent.internal.core.dom.Identifier;
import dtool.dom.ast.ASTNode;

public class DefSymbol extends Symbol {

	public DefSymbol(Identifier id, DefUnit parent) {
		super(id);
		setParent(parent);
	}

	public DefSymbol(String name, DefUnit parent) {
		super(name);
		setParent(parent);
	}
	
	@Override
	public DefUnit getParent() {
		return (DefUnit) super.getParent();
	}

}
