package descent.internal.compiler.parser;

public class SliceExp extends UnaExp {

	public Expression lwr;
	public Expression upr;
	public VarDeclaration lengthVar;

	public SliceExp(Expression e1, Expression lwr, Expression upr) {
		super(TOK.TOKslice, e1);
		this.lwr = lwr;
		this.upr = upr;
	}
	
	@Override
	public int getNodeType() {
		return SLICE_EXP;
	}

}
