package descent.internal.compiler.parser;

import java.util.ArrayList;

public class Arguments extends ArrayList<Argument> {

	private static final long serialVersionUID = 1L;
	
	public Arguments() {
	}
	
	public Arguments(int capacity) {
		super(capacity);
	}
	
	public Arguments(Arguments elements) {
		super(elements);
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
