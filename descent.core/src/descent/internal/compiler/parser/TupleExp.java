package descent.internal.compiler.parser;

import java.util.List;

public class TupleExp extends Expression {
	
	public List<Expression> exps;

    public TupleExp(List<Expression> exps) {
    	super(TOK.TOKtuple);
    	this.exps = exps;
    }
    
    @Override
    public Expression castTo(Scope sc, Type t, SemanticContext context) {
		for (int i = 0; i < exps.size(); i++) {
			Expression e = (Expression) exps.get(i);
			e = e.castTo(sc, t, context);
			exps.set(i, e);
		}
		return this;
	}

	@Override
	public int getNodeType() {
		return TUPLE_EXP;
	}

}
