package util;

public class Logger {
	
	public static boolean debug = false;

	static {
	}

	public static void printErr(String string) {
		System.err.print(string);
	}

	public static void print(String string) {
		System.out.print(string);
	}
}
