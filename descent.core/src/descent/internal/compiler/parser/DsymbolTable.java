package descent.internal.compiler.parser;

// DMD 1.020
public class DsymbolTable {
	
	private HashtableOfCharArrayAndObject map = new HashtableOfCharArrayAndObject();
	
	public Dsymbol insert(Dsymbol dsymbol) {
		return insert(dsymbol.ident, dsymbol);
	}
	
	public Dsymbol insert(IdentifierExp ident, Dsymbol dsymbol) {
		if (map.containsKey(ident.ident)) {
			return null;
		}
		map.put(ident.ident, dsymbol);
		return dsymbol;
	}

	public Dsymbol lookup(IdentifierExp ident) {
		return (Dsymbol) map.get(ident.ident);
	}
	
	public Dsymbol lookup(char[] ident) {
		return (Dsymbol) map.get(ident);
	}
	
	@Override
	public String toString() {
		return map.toString();
	}

}
