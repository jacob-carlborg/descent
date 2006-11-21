package descent.internal.core.dom;

public class FuncLiteralDeclaration extends FuncDeclaration {

	public FuncLiteralDeclaration(Loc loc, int endloc, Type type,
			TOK tok, ForeachStatement fes) {
		super(loc, endloc, null, STC.STCundefined, type);
	}

}
