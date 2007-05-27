package descent.internal.compiler.parser;

import java.util.HashMap;
import java.util.Map;

public class DsymbolTable {
	
	private Map<String, Dsymbol> map = new HashMap<String, Dsymbol>();
	
	public Dsymbol insert(Dsymbol dsymbol) {
		if (map.containsKey(dsymbol.ident.ident)) {
			return null;
		}
		map.put(dsymbol.ident.ident, dsymbol);
		return dsymbol;
	}

	public Dsymbol lookup(IdentifierExp ident) {
		return map.get(ident.ident);
	}
	
	public Dsymbol lookup(String ident) {
		return map.get(ident);
	}

}
