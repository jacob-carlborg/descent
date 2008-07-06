package descent.internal.compiler.parser;

// DMD 1.020
public class ThisDeclaration extends VarDeclaration {

	public ThisDeclaration(Loc loc, Type type) {
		super(loc, type, Id.This, null);
	}
	
	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		throw new IllegalStateException("assert(0);");
	}

}
