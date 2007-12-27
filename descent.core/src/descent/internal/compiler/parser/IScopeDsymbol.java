package descent.internal.compiler.parser;

public interface IScopeDsymbol extends IDsymbol {
	
	void importScope(IScopeDsymbol s, PROT protection);
	
	IDsymbolTable symtab();
	
	void symtab(IDsymbolTable symtab);
	
	Dsymbols members();
	
	void members(Dsymbols members);

}
