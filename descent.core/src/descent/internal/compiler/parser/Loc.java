package descent.internal.compiler.parser;

// DMD 1.020
public class Loc {

	public final static Loc ZERO = new Loc();

	public int linnum;
	public char[] filename;

	public Loc() {
		this.linnum = 0;
		this.filename = null;
	}

	public Loc(int linnum) {
		this.linnum = linnum;
		this.filename = null;
	}
	
	public Loc(char[] filename, int linnum) {
		this.linnum = linnum;
		this.filename = filename;
	}

	public String toChars() {
		OutBuffer buf = new OutBuffer();

		if (filename != null) {
			buf.writestring(filename);
		}
//
//		if (linnum != 0) {
//			buf.writestring("(");
//			buf.writestring(linnum);
//			buf.writestring(")");
//		}
		return buf.extractData();
	}

}
