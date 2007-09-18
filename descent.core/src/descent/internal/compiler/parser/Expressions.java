package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

// DMD 1.020
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
	
	/**
	 * Reserves the given ammount of space. Slow, so another method should be
	 * employed where possible, but since DMD uses this construct a lot, it'
	 * makes porting much easier in places where DMD does more than just fill
	 * up the list.
	 * 
	 * @param dim
	 */
	public void setDim(int dim)
	{
		for(int i = 0; i < dim; i++)
				add(null);
	}

}
