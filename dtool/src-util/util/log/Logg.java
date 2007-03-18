package util.log;

import java.io.PrintStream;

public class Logg {

	public static boolean debug = true;

	public static PrintStream err = System.out; 
	
	static {
	}

	public static void println(Object string) {
		print(string);
		println();
	}

	public static void print(Object string) {
		if (debug)
			System.out.print(string);
	}

	private static void println() {
		if (debug)
			System.out.println();
	}

}
