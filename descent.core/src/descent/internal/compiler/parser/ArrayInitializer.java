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
	public int kind() {
		return ARRAY_INITIALIZER;
	}

}
