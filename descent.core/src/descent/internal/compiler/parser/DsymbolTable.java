package descent.internal.compiler.parser;


// DMD 1.020
public class DsymbolTable implements IDsymbolTable {
	
	private HashtableOfCharArrayAndObject map = new HashtableOfCharArrayAndObject();
	
	public DsymbolTable() {
	}
	
	public DsymbolTable(IDsymbolTable table) {
		for(char[] key : table.keys()) {
			if (key == null) {
				continue;
			}
			
			insert(key, table.lookup(key));
		}
	}

	/* (non-Javadoc)
	 * @see descent.internal.compiler.parser.IDsymbolTable#insert(descent.internal.compiler.parser.IDsymbol)
	 */
	public IDsymbol insert(IDsymbol dsymbol) {
		return insert(dsymbol.ident(), dsymbol);
	}
	
	/* (non-Javadoc)
	 * @see descent.internal.compiler.parser.IDsymbolTable#insert(descent.internal.compiler.parser.IdentifierExp, descent.internal.compiler.parser.IDsymbol)
	 */
	public IDsymbol insert(IdentifierExp ident, IDsymbol dsymbol) {
		return insert(ident.ident, dsymbol);
	}
	
	/* (non-Javadoc)
	 * @see descent.internal.compiler.parser.IDsymbolTable#insert(char[], descent.internal.compiler.parser.IDsymbol)
	 */
	public IDsymbol insert(char[] ident, IDsymbol dsymbol) {
		if (map.containsKey(ident)) {
			return null;
		}
		map.put(ident, dsymbol);
		return dsymbol;
	}

	/* (non-Javadoc)
	 * @see descent.internal.compiler.parser.IDsymbolTable#lookup(descent.internal.compiler.parser.IdentifierExp)
	 */
	public IDsymbol lookup(IdentifierExp ident) {
		return (IDsymbol) map.get(ident.ident);
	}
	
	/* (non-Javadoc)
	 * @see descent.internal.compiler.parser.IDsymbolTable#lookup(char[])
	 */
	public IDsymbol lookup(char[] ident) {
		return (IDsymbol) map.get(ident);
	}
	
	/* (non-Javadoc)
	 * @see descent.internal.compiler.parser.IDsymbolTable#keys()
	 */
	public char[][] keys() {
		return map.keys();
	}
	
	@Override
	public String toString() {
		return map.toString();
	}

}
