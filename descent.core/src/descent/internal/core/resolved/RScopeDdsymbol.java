package descent.internal.core.resolved;

import descent.internal.compiler.parser.DsymbolTable;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.IArrayScopeSymbol;
import descent.internal.compiler.parser.IScopeDsymbol;
import descent.internal.compiler.parser.PROT;

public class RScopeDdsymbol extends RDsymbol implements IScopeDsymbol {

	public void importScope(IScopeDsymbol s, PROT protection) {
	}

	public IArrayScopeSymbol isArrayScopeSymbol() {
		return null;
	}

	public Dsymbols members() {
		return null;
	}

	public void members(Dsymbols members) {
	}

	public DsymbolTable symtab() {
		return null;
	}

	public void symtab(DsymbolTable symtab) {
	}

}
