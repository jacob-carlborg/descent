package descent.internal.compiler.parser;

public class TypeDotIdExp extends Expression {

	public IdentifierExp ident;

	public TypeDotIdExp(Loc loc, Type type, IdentifierExp ident) {
		super(loc, TOK.TOKtypedot);
		this.type = type;
		this.ident = ident;
	}

	@Override
	public int getNodeType() {
		return TYPE_DOT_ID_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;

		e = new DotIdExp(loc, new TypeExp(loc, type), ident);
		e = e.semantic(sc, context);
		return e;
	}
	
	@Override
	public Expression syntaxCopy() {
		TypeDotIdExp te = new TypeDotIdExp(loc, type.syntaxCopy(), ident);
		return te;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writeByte('(');
	    type.toCBuffer(buf, null, hgs);
	    buf.writeByte(')');
	    buf.writeByte('.');
	    buf.writestring(ident.toChars());
	}

}
