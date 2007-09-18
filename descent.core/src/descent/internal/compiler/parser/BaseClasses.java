package descent.internal.compiler.parser;

import java.util.ArrayList;

// DMD 1.020
public class BaseClasses extends ArrayList<BaseClass> {

	private static final long serialVersionUID = 1L;
	
	public BaseClasses() {
	}
	
	public BaseClasses(int capacity) {
		super(capacity);
	}
	
	public BaseClasses(BaseClasses elements) {
		super(elements);
	}

}
