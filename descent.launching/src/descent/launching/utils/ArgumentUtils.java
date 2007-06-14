package descent.launching.utils;

/**
 * Utilities for string arguments.
 */
public class ArgumentUtils {
	
	/**
	 * If the path contains spaces, the string is returned
	 * eclosed with double quotes. Otherwise, the string
	 * is returned without modification.
	 * @param string a string
	 * @return the resulting string
	 */
	public static String toStringArgument(String string) {
		if (string.indexOf(' ') != -1) {
			return "\"" + string + "\"";
		} else {
			return string;
		}
	}

}
