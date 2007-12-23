package descent.internal.compiler.parser;


// DMD 1.020
public class DsymbolTable {
	
	private HashtableOfCharArrayAndObject map = new HashtableOfCharArrayAndObject();
	
	public IDsymbol insert(IDsymbol dsymbol) {
		return insert(dsymbol.ident(), dsymbol);
	}
	
	public IDsymbol insert(IdentifierExp ident, IDsymbol dsymbol) {
		return insert(ident.ident, dsymbol);
	}
	
	public IDsymbol insert(char[] ident, IDsymbol dsymbol) {
		if (map.containsKey(ident)) {
			return null;
		}
		map.put(ident, dsymbol);
		return dsymbol;
	}

	public IDsymbol lookup(IdentifierExp ident) {
		return (Dsymbol) map.get(ident.ident);
	}
	
	public IDsymbol lookup(char[] ident) {
		return (Dsymbol) map.get(ident);
	}
	
	/**
	 * Note: null entries may be present in the returned array.
	 */
	public char[][] keys() {
		return map.keys();
	}
	
	@Override
	public String toString() {
		return map.toString();
	}

}
