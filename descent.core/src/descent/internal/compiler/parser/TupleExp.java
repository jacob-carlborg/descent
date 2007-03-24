package descent.internal.compiler.parser;

import java.util.List;

public class TupleExp extends Expression {
	
	public List<Expression> exps;

    public TupleExp(List<Expression> exps) {
    	super(TOK.TOKtuple);
    	this.exps = exps;
    }

	@Override
	public int getNodeType() {
		return TUPLE_EXP;
	}

}
