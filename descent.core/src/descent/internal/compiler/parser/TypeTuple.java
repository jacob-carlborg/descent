package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public class TypeTuple extends Type {

	public List<Argument> arguments;

	private TypeTuple() {
		super(TY.Ttuple, null);
	}

	public static TypeTuple newArguments(List<Argument> arguments) {
		TypeTuple tt = new TypeTuple();
		tt.arguments = arguments;
		return tt;
	}

	public static TypeTuple newExpressions(List<Expression> exps, SemanticContext context) {
		TypeTuple tt = new TypeTuple();
		ArrayList<Argument> arguments = new ArrayList<Argument>();
		if (exps != null) {
			arguments.ensureCapacity(exps.size());
			for (int i = 0; i < exps.size(); i++) {
				Expression e = exps.get(i);
				if (e.type.ty == TY.Ttuple) {
					e.error("cannot form tuple of tuples");
				}
				Argument arg = new Argument(InOut.In, e.type, null, null);
				arguments.set(i, arg);
			}
		}
		tt.arguments = arguments;
		return tt;
	}

	@Override
	public int getNodeType() {
		return TYPE_TUPLE;
	}

}
