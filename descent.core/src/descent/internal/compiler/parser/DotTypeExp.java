package descent.internal.compiler.parser;

public class DotTypeExp extends UnaExp {
	
	public Dsymbol sym;

	public DotTypeExp(Loc loc, Expression e, Dsymbol s) {
		super(loc, TOK.TOKdottype, e);
		this.sym = s;
		this.type = s.getType();
	}
	
	@Override
	public int getNodeType() {
		return 0;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		super.semantic(sc, context);
	    return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		expToCBuffer(buf, hgs, e1, PREC.PREC_primary, context);
	    buf.writeByte('.');
	    buf.writestring(sym.toChars());
	}

}
