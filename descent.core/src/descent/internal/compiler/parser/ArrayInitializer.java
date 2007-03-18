package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

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
					error("cannot infer type from this array initializer");
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

		// Lno:
		error("cannot infer type from this array initializer");
		return Type.terror;
	}
	
	@Override
	public int kind() {
		return ARRAY_INITIALIZER;
	}

}
