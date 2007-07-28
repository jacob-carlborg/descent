package descent.internal.compiler.parser;

import java.util.List;

// TODO semantic
public class AssocArrayLiteralExp extends Expression {
	
	public List<Expression> keys;
	public List<Expression> values;

	public AssocArrayLiteralExp(Loc loc, List<Expression> keys, List<Expression> values) {
		super(loc, TOK.TOKassocarrayliteral);
		this.keys = keys;
		this.values = values;
	}

	@Override
	public int getNodeType() {
		return ASSOC_ARRAY_LITERAL_EXP;
	}

}
