package descent.internal.compiler.parser;



public class DsymbolTable {
	
	private HashtableOfCharArrayAndObject map = new HashtableOfCharArrayAndObject();
	
	public DsymbolTable() {
	}
	
	public DsymbolTable(DsymbolTable table) {
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
	public Dsymbol insert(Dsymbol dsymbol) {
		return insert(dsymbol.ident, dsymbol);
	}
	
	/* (non-Javadoc)
	 * @see descent.internal.compiler.parser.IDsymbolTable#insert(descent.internal.compiler.parser.IdentifierExp, descent.internal.compiler.parser.IDsymbol)
	 */
	public Dsymbol insert(IdentifierExp ident, Dsymbol dsymbol) {
		return insert(ident.ident, dsymbol);
	}
	
	/* (non-Javadoc)
	 * @see descent.internal.compiler.parser.IDsymbolTable#insert(char[], descent.internal.compiler.parser.IDsymbol)
	 */
	public Dsymbol insert(char[] ident, Dsymbol dsymbol) {
		if (map.containsKey(ident)) {
			return null;
		}
		map.put(ident, dsymbol);
		return dsymbol;
	}

	/* (non-Javadoc)
	 * @see descent.internal.compiler.parser.IDsymbolTable#lookup(descent.internal.compiler.parser.IdentifierExp)
	 */
	public Dsymbol lookup(IdentifierExp ident) {
		return (Dsymbol) map.get(ident.ident);
	}
	
	/* (non-Javadoc)
	 * @see descent.internal.compiler.parser.IDsymbolTable#lookup(char[])
	 */
	public Dsymbol lookup(char[] ident) {
		return (Dsymbol) map.get(ident);
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
