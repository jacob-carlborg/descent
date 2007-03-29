package descent.internal.compiler.parser;

public class ComplexExp extends Expression {
	
	public ComplexExp(TOK op) {
		super(op);
	}

	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		if (type.iscomplex() && t.iscomplex())
			type = t;
		else
			return super.castTo(sc, t, context);
		return this;
	}

	@Override
	public int getNodeType() {
		return COMPLEX_EXP;
	}	
	

}
