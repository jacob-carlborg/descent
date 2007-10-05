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
		// PERHAPS semantic (i'm not sure if this is even ever used)
		return "";
	}

}
