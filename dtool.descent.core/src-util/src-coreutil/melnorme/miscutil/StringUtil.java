package melnorme.miscutil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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

	
	/** Create a String from the given coll with a given separator String */	
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
	
	/** Creates a String array where each element is the to toString()
	 * of each element of the given collection. */
	public static String[] collToStringArray(List<?> coll) {
		if(coll == null) 
			return new String[0];
		String[] strs = new String[coll.size()];
		Iterator<?> iter = coll.iterator();
		for (int i = 0; i < strs.length; i++) {
			strs[i] = iter.next().toString();
		}
		return strs;
	}

	/** Returns str with the given range (repOffset and repLen) substituted 
	 * for repStr. */
	public static String replaceStr(String str, int repOffset, int repLen,
			String repStr) {
		return str.substring(0, repOffset) + repStr
				+ str.substring(repOffset + repLen, str.length());
	}

	/** Replace str with strRep in the given strb StringBuilder, if str occurs.
	 * Return true if str occurs in strb. */
	public static boolean replace(StringBuilder strb, String str, String repStr) {
		int ix = strb.indexOf(str);
		if(ix != -1) {
			strb.replace(ix, ix + str.length(), repStr);
			return true;
		}
		return false;
	}

	/** Return "" if string is null, or the unmodified string otherwise. */
	public static String nullAsEmpty(String string) {
		if(string == null)
			return "";
		return string;
	}


}
