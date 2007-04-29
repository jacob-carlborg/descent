package descent.internal.compiler.parser;

public class DotTemplateExp extends UnaExp {

	public TemplateDeclaration td;

	public DotTemplateExp(Loc loc, Expression e1, TemplateDeclaration td) {
		super(loc, TOK.TOKdottd, e1);
		this.td = td;
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		expToCBuffer(buf, hgs, e1, PREC.PREC_primary, context);
	    buf.writeByte('.');
	    buf.writestring(td.toChars());
	}

	@Override
	public int getNodeType() {
		return 0;
	}

}
