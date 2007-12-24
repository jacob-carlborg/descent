package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.STC.*;

// DMD 1.020
public class SymbolDeclaration extends Declaration implements ISymbolDeclaration {
	
	public Symbol sym;
	public IStructDeclaration dsym;
	
	public SymbolDeclaration(Loc loc, Symbol s, IStructDeclaration dsym) {
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
    
    public IStructDeclaration dsym() {
    	return dsym;
    }
    
    public Symbol sym() {
    	return sym;
    }
	
}
