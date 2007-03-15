package descent.internal.compiler.parser;

public class Chars {
	
	private static int cmtable[];
	
	private final static int CMoctal = 0x1;
	private final static int CMhex = 0x2;
	private final static int CMidchar = 0x4;
	
	static {
		cmtable = new int[256];
		 for (int c = 0; c < cmtable.length; c++)
	    {
		if ('0' <= c && c <= '7')
		    cmtable[c] |= CMoctal;
		if (isdigit(c) || ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F'))
		    cmtable[c] |= CMhex;
		if (isalnum(c) || c == '_')
		    cmtable[c] |= CMidchar;
	    }
	}
	
	public static boolean isidstart(int c) {
		return isalpha(c) || c == '_';
	}
	
	public static boolean isidchar(int c) {
		return (cmtable[c] & CMidchar) != 0;
	}
	
	public static boolean ishex(int c) {
		return (cmtable[c] & CMhex) != 0;
	}
	
	public static boolean isoctal(int c) {
		return (cmtable[c] & CMoctal) != 0;
	}
	
	public static boolean islower(int c) {
		return 'a' <= c && c <= 'z';
	}
	
	public static boolean isdigit(int c) {
		return '0' <= c && c <= '9';
	}
	
	public static boolean isalnum(int c) {
		return ('a' <= c && c <= 'z') || 
			('A' <= c && c <= 'Z') ||
			('0' <= c && c <= '9');
	}
	
	public static boolean isalpha(int c) {
		return ('a' <= c && c <= 'z') || 
			('A' <= c && c <= 'Z');
	}
	
	public static boolean isprint(int c) {
		return 0x20 <= c && c <= 0x7E;
	}

}
