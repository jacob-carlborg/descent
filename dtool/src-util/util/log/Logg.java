package util.log;

public class Logg {

	public static boolean debug = true;

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
