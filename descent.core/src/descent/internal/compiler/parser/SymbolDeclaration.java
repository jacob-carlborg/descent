package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.STC.*;

// DMD 1.020
public class SymbolDeclaration extends Declaration {
	
	public Symbol sym;
	public StructDeclaration dsym;
	
	public SymbolDeclaration(Loc loc, Symbol s, StructDeclaration dsym) {
		super(loc, new IdentifierExp(s.Sident));
		sym = s;
		this.dsym = dsym;
		storage_class |= STCconst;
	}

    public SymbolDeclaration isSymbolDeclaration()
    {
        return this;
    }
	
}
