package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.STC.*;

// DMD 1.020
public class SymbolDeclaration extends Declaration {
	
	public Symbol sym;
	public StructDeclaration dsym;
	
	public SymbolDeclaration(Loc loc, Symbol s, StructDeclaration dsym) {
		super(new IdentifierExp(s.Sident));
		this.loc = loc;
		sym = s;
		this.dsym = dsym;
		storage_class |= STCconst;
	}

    public SymbolDeclaration isSymbolDeclaration()
    {
        return this;
    }
	
}