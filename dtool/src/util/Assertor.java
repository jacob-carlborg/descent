package util;


public class Assertor {

	public static void abort(String string) {
		throw new RuntimeException("ABORT:"+string);
	}

}
