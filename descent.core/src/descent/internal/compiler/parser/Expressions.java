package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public class Expressions extends ArrayList<Expression> {

	private static final long serialVersionUID = 1L;
	
	public Expressions() {
	}
	
	public Expressions(int capacity) {
		super(capacity);
	}
	
	public Expressions(Expressions objects) {
		super(objects);
	}
	
	@Override
	public List<Expression> subList(int fromIndex, int toIndex) {
		return super.subList(fromIndex, toIndex);
	}

}
