package melnorme.miscutil;

import java.util.Arrays;
import java.util.Collection;
/**
 * Miscelleanous String utilities 
 */
public final class StringUtil {

	/* ****************************************************** */
	
	public static String newSpaceFilledString(int indent) {
		return newFilledString(indent, ' ');
	}

	public static String newFilledString(int indent, char ch) {
		char str[] = new char[indent];
		Arrays.fill(str, ch);
		return new String(str);
	}

	public static String newFilledString(int indent, String str) { 
		StringBuffer sb = new StringBuffer(indent);
		for (int i = 0; i < indent; i++ )
			sb = sb.append(str);
		
		return sb.toString();
	}

	/**
	 * Append a string if the string isn't empty
	 */
	public static String trailString(String str, String strtrail) {
		if(str.length() > 0)
			return str + strtrail;
		else 
			return str;
	}	
	
	
	
	/**
	 * Returns a String of the given Collection elements with the 
	 * given separator String.
	 */	
	public static String collToString(Collection<?> coll, String sep) {
		//if(coll == null) return "<null>";
		
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(Object item : coll){
			if(!first)
				sb.append(sep);
			else
				first = false;
			sb.append(item.toString());
		}
		return sb.toString();
	}

	
	/** Prints an array with given separator String */	
	public static String collToString(Object[] coll, String sep) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(Object item : coll){
			if(!first)
				sb.append(sep);
			else
				first = false;
			sb.append(item.toString());
		}
		return sb.toString();
	}

	/** Returns str with the given range (repOffset and repLen) substituted 
	 * for repStr. */
	public static String replaceStr(String str, int repOffset, int repLen,
			String repStr) {
		return str.substring(0, repOffset) + repStr
				+ str.substring(repOffset + repLen, str.length());
	}
}
