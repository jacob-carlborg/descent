package util.log;

import java.io.PrintStream;

public class Logg {

	public static class NullPrinter {
		public void println(Object string) {
			// Do nothing
		}

		public void print(Object string) {
			// Do nothing
		}

		public void println() {
			// Do nothing
		}
	}

	public static boolean debug = true;

	public static PrintStream out = System.out; 
	public static PrintStream err = System.err; 
	public static NullPrinter not = new NullPrinter();

	public static void println(Object string) {
		print(string);
		println();
	}

	public static void print(Object string) {
		if (debug)
			System.out.print(string);
	}

	public static void println() {
		if (debug)
			System.out.println();
	}

}
