package descent.core;

public class DeeConventions {

	public static boolean isValidIdentifier(String string) {
		return JavaConventions.scannedIdentifier(string) != null;
	}

}
