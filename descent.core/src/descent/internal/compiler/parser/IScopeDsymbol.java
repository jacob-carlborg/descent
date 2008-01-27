package descent.internal.compiler.parser;

import java.util.List;

public interface IScopeDsymbol extends IDsymbol {
	
	void importScope(IScopeDsymbol s, PROT protection);
	
	IDsymbolTable symtab();
	
	void symtab(IDsymbolTable symtab);
	
	Dsymbols members();
	
	void members(Dsymbols members);

	List<IScopeDsymbol> imports();
	
	void imports(List<IScopeDsymbol> imports);
	
	List<PROT> prots();
	
	void prots(List<PROT> prots);

}
