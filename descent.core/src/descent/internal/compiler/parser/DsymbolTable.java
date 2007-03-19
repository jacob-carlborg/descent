package descent.internal.compiler.parser;

import java.util.HashMap;
import java.util.Map;

public class DsymbolTable {
	
	private Map<String, Dsymbol> map = new HashMap<String, Dsymbol>();
	
	public Dsymbol insert(Dsymbol dsymbol) {
		if (map.containsKey(dsymbol.ident.ident.string)) {
			return null;
		}
		map.put(dsymbol.ident.ident.string, dsymbol);
		return dsymbol;
	}

	public Dsymbol lookup(IdentifierExp ident) {
		return map.get(ident.ident.string);
	}
	
	public Dsymbol lookup(Identifier ident) {
		return map.get(ident.string);
	}

}
