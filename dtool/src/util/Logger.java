package util;

public class Logger {
	
	public static boolean debug = false;

	static {
	}

	public static void print(String string) {
		System.out.print(string);
	}

	public static void printErr(String string) {
		System.err.print(string);
	}

	public static void printDebug(String string) {
		if(debug)
			System.out.print(string);
	}
}
