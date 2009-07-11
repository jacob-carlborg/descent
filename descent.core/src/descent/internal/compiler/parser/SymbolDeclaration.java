package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.STC.*;


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

	public SymbolDeclaration isSymbolDeclaration() {
		return this;
	}

	public StructDeclaration dsym() {
		return dsym;
	}

	public Symbol sym() {
		return sym;
	}

	@Override
	public String getSignature(int options) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char getSignaturePrefix() {
		// TODO Auto-generated method stub
		return super.getSignaturePrefix();
	}

}
