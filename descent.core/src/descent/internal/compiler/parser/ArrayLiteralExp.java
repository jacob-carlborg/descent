package descent.internal.compiler.parser;

import java.util.List;

public class ArrayLiteralExp extends Expression {

	public List<Expression> elements;

	public ArrayLiteralExp(List<Expression> elements) {
		super(TOK.TOKarrayliteral);
		this.elements = elements;
	}
	
	@Override
	public boolean isBool(boolean result) {
		int dim = elements != null ? elements.size() : 0;
	    return result ? (dim != 0) : (dim == 0);
	}
	
	@Override
	public int getNodeType() {
		return ARRAY_LITERAL_EXP;
	}

}
