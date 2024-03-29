/*******************************************************************************
 * Copyright (c) 2007 DSource.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial implementation
 *******************************************************************************/
package melnorme.utilbox.misc;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Miscelleanous String utilities 
 */
public final class StringUtil {
	
	public static final Charset UTF8 = Charset.forName("UTF-8");
	public static final Charset UTF16 = Charset.forName("UTF-16");
	
	
	/** @return a String of the given Collection elements with the given separator String. */
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

	
	/** @return a String from the given coll with a given separator String. */	
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

	/** @return str with the given range (repOffset and repLen) substituted for repStr. */
	public static String replaceStr(String str, int repOffset, int repLen,
			String repStr) {
		return str.substring(0, repOffset) + repStr
				+ str.substring(repOffset + repLen, str.length());
	}

	/** Replace str with strRep in the given strb StringBuilder, if str occurs.
	 * @return true if str occurs in strb. */
	public static boolean replace(StringBuilder strb, String str, String repStr) {
		int ix = strb.indexOf(str);
		if(ix != -1) {
			strb.replace(ix, ix + str.length(), repStr);
			return true;
		}
		return false;
	}

	/** @return "" if string is null, or the given string otherwise. */
	public static String nullAsEmpty(String string) {
		if(string == null)
			return "";
		return string;
	}

	/** @return given string with strtrail appended, if given string isn't empty. */
	public static String trailString(String string, String strtrail) {
		if(string.length() > 0)
			return string + strtrail;
		else 
			return string;
	}
	
	/** @return a substring of given string up until the beggining of first match 
	 * of given match, or the whole string if no match is found. */
	public static String upUntil(String string, String match) {
		final int index = string.indexOf(match);
		return (index == -1) ? string : string.substring(0, index);
	}
	
	/** @return a substring of given string starting from the end of the last occurrence 
	 * of given match, or the whole string if no match is found. */
	public static String fromLastIndexOf(String match, String string) {
		int lastIx = string.lastIndexOf(match);
		return (lastIx == -1) ? string : string.substring(lastIx + match.length());
	}

	/** @return a copy of given string without leading spaces. */
	public static String trimLeadingSpaces(String string) {
		int pos = 0;
		while(pos < string.length() && string.charAt(pos) == ' ')
			pos++;
		return string.substring(pos);
	}

	/** @return a String of given length filled with spaces. */
	public static String newSpaceFilledString(int length) {
		return newFilledString(length, ' ');
	}

	/** @return a String of given length filled with given ch. */
	public static String newFilledString(int length, char ch) {
		char str[] = new char[length];
		Arrays.fill(str, ch);
		return new String(str);
	}

	/** @return a String of given length filled with given str. */
	public static String newFilledString(int length, String str) { 
		StringBuffer sb = new StringBuffer(length * str.length());
		for (int i = 0; i < length; i++ )
			sb = sb.append(str);
		
		return sb.toString();
	}

}
