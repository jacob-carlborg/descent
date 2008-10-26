package mmrnmhrm.core.dltk.search;

import static melnorme.miscutil.Assert.assertFail;
import static melnorme.miscutil.Assert.assertFailTODO;

import melnorme.miscutil.ArrayUtil;
import melnorme.miscutil.StringUtil;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.ISearchPatternProcessor;

public class DeeSearchPatterProcessor implements ISearchPatternProcessor {

	public static final DeeSearchPatterProcessor instance = new DeeSearchPatterProcessor();
	
	private static final String TYPE_DELIMITER = ".";
	private static final String METHOD_DELIMITER = ".";
	
	public String getDelimiterReplacementString() {
		return TYPE_DELIMITER;
	}
	
	public static String substringUntil(String string, String delimiter) {
		final int pos = string.lastIndexOf(delimiter);
		if (pos != -1) {
			return string.substring(0, pos);
		}
		return null;
	}
	
	public static String substringFrom(String string, String delimiter) {
		final int pos = string.lastIndexOf(delimiter);
		if (pos != -1) {
			return string.substring(pos + delimiter.length());
		}
		return null;
	}
	
	// Method pattern operations
	public char[] extractDeclaringTypeQualification(String pattern) {
		String type = substringUntil(pattern, METHOD_DELIMITER);
		if (type != null) {
			return extractTypeQualification(type);
		}
		return null;
	}


	public char[] extractDeclaringTypeSimpleName(String pattern) {
		String type = substringUntil(pattern, METHOD_DELIMITER);
		if (type != null) {
			return extractTypeChars(type).toCharArray();
		}
		return null;
	}

	public char[] extractSelector(String pattern) {
		String selector = substringFrom(pattern, METHOD_DELIMITER);
		if(selector == null)
			return pattern.toCharArray();
		return selector.toCharArray();
	}

	// Type pattern operations
	public String extractTypeChars(String pattern) {
		String simpleName = substringFrom(pattern, TYPE_DELIMITER);
		if (simpleName != null) {
			return simpleName;
		}
		return pattern;
	}


	public char[] extractTypeQualification(String pattern) {
		char[] rawTypeQualification = extractRawTypeQualification(pattern);
		if(rawTypeQualification == null)
			return null;
		// Have no ideia why this '$' substitution is made
		return CharOperation.replace(rawTypeQualification, TYPE_DELIMITER.toCharArray(),
					new char[] { '$' });
	}
	private char[] extractRawTypeQualification(String pattern) {
		String typeQual = substringUntil(pattern, TYPE_DELIMITER);
		if (typeQual != null) {
			return typeQual.toCharArray();
		}
		return null;
	}
	
	// Field pattern operations

}