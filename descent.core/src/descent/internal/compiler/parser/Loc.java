package descent.internal.compiler.parser;

// DMD 1.020
public class Loc {
	
	public final static Loc ZERO = new Loc();
	
	public String filename;
	
	public Loc() {
	}
	
	public Loc(int value) {
		
	}

	public String toChars() {
		// TODO semantic
		return null;
	}

}
