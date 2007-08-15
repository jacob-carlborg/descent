package dtool.dom.definitions;

import descent.internal.compiler.parser.IdentifierExp;

public class DefSymbol extends Symbol {

	public DefSymbol(IdentifierExp id, DefUnit parent) {
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
