package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public class TypeSArray extends Type {
	
	public Expression dim;

	public TypeSArray(Type next, Expression dim) {
		super(TY.Tsarray, next);
		this.dim = dim;
	}
	
	@Override
	public Expression toExpression() {
		Expression e = next.toExpression();
		if (e != null) {
			List<Expression> arguments = new ArrayList<Expression>(1);
			arguments.add(dim);
			e = new ArrayExp(e, arguments);
			e.setSourceRange(start, length);
		}
		return e;
	}
	
	@Override
	public int kind() {
		return TYPE_S_ARRAY;
	}

}
