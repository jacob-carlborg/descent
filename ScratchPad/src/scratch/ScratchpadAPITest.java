package scratch;

import foo.Xpto;

public class ScratchpadAPITest {
	
	/**
	 */
	public static Exception getSomeException() {
		return null;
	}
	
	/**
	 * @since 1.1
	 */
	public static Exception getSomeException2() {
		return null;
	}
	
	/**
	 * IGNORED
	 */
	public static Xpto getSomeXpto() {
		return new Xpto();
	}
	
	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public static Xpto getSomeXpto2() {
		return new Xpto();
	}
}
