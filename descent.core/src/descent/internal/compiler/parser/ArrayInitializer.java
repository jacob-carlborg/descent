package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IProblem;

public class ArrayInitializer extends Initializer {
	
	public List<Expression> index;
	public List<Initializer> value;
	
	public void addInit(Expression index, Initializer value) {
		if (this.index == null) {
			this.index = new ArrayList<Expression>();
			this.value = new ArrayList<Initializer>();
		}
		this.index.add(index);
		this.value.add(value);
	}
	
	@Override
	public Type inferType(Scope sc, SemanticContext context) {
		if (value != null) {
			for (int i = 0; i < value.size(); i++) {
				if (index.get(i) != null) {
					// goto Lno;
					context.acceptProblem(Problem.newSemanticTypeError("Cannot infer type from this array initializer", IProblem.CannotInferType, 0, start, length));
					return Type.terror;
				}
			}
			if (value.size() > 0) {
				Initializer iz = (Initializer) value.get(0);
				if (iz != null) {
					Type t = iz.inferType(sc, context);
					t = new TypeSArray(t, new IntegerExp(value.size()));
					t = t.semantic(sc, context);
					return t;
				}
			}
		}
		
		context.acceptProblem(Problem.newSemanticTypeError("Cannot infer type from this array initializer", IProblem.CannotInferType, 0, start, length));
		return Type.terror;
	}
	
	@Override
	public int getNodeType() {
		return ARRAY_INITIALIZER;
	}

}
