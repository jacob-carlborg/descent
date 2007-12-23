package descent.internal.compiler.parser;

public interface IDsymbolTable {

	public abstract IDsymbol insert(IDsymbol dsymbol);

	public abstract IDsymbol insert(IdentifierExp ident, IDsymbol dsymbol);

	public abstract IDsymbol insert(char[] ident, IDsymbol dsymbol);

	public abstract IDsymbol lookup(IdentifierExp ident);

	public abstract IDsymbol lookup(char[] ident);

	/**
	 * Note: null entries may be present in the returned array.
	 */
	public abstract char[][] keys();

}