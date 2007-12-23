package descent.internal.compiler.parser;

public interface IScopeDsymbol extends IDsymbol {
	
	void importScope(IScopeDsymbol s, PROT protection);
	
	DsymbolTable symtab();
	
	void symtab(DsymbolTable symtab);
	
	Dsymbols members();
	
	void members(Dsymbols members);

	IArrayScopeSymbol isArrayScopeSymbol();

}
