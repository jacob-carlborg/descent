package descent.internal.compiler.parser;

import java.util.List;

public class TypeTuple extends Type {
	
	public List<Argument> arguments;

	public TypeTuple(List<Argument> arguments) {
		super(TY.Ttuple, null);
		this.arguments = arguments;
	}

	@Override
	public int kind() {
		return TYPE_TUPLE;
	}

}
