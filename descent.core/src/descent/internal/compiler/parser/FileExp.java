package descent.internal.compiler.parser;

public class FileExp extends UnaExp {

	public FileExp(Loc loc, Expression e) {
		super(loc, TOK.TOKmixin, e);
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring("import(");
	    expToCBuffer(buf, hgs, e1, PREC.PREC_assign, context);
	    buf.writeByte(')');
	}
	
	@Override
	public int getNodeType() {
		return FILE_EXP;
	}

}
