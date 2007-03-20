package descent.internal.compiler.parser;

import java.util.List;

public class ArrayLiteralExp extends Expression {

	public List<Expression> elements;

	public ArrayLiteralExp(List<Expression> elements) {
		super(TOK.TOKarrayliteral);
		this.elements = elements;
	}
	
	@Override
	public int getNodeType() {
		return ARRAY_LITERAL_EXP;
	}

}
