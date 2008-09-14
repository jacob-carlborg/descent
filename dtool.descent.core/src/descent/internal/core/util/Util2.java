package descent.internal.core.util;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

import descent.core.JavaCore;
import descent.core.Signature;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.util.SuffixConstants;

public class Util2 {

	/**
	 * Converts a String[] to char[][].
	 */
	public static char[][] toCharArrays(String[] a) {
		int len = a.length;
		if (len == 0) return CharOperation.NO_CHAR_CHAR;
		char[][] result = new char[len][];
		for (int i = 0; i < len; ++i) {
			result[i] = a[i].toCharArray();
		}
		return result;
	}

	/**
	 * Returns true if the given name ends with one of the known java like extension.
	 * (implementation is not creating extra strings)
	 */
	public final static boolean isJavaLikeFileName(String name) {
		if (name == null) return false;
		return Util2.indexOfJavaLikeExtension(name) != -1;
	}

	/**
	 * Returns true if the given name ends with one of the known java like extension.
	 * (implementation is not creating extra strings)
	 */
	public final static boolean isJavaLikeFileName(char[] fileName) {
		if (fileName == null) return false;
		int fileNameLength = fileName.length;
		char[][] javaLikeExtensions = Util2.getJavaLikeExtensions();
		extensions: for (int i = 0, length = javaLikeExtensions.length; i < length; i++) {
			char[] extension = javaLikeExtensions[i];
			int extensionLength = extension.length;
			int extensionStart = fileNameLength - extensionLength;
			if (extensionStart-1 < 0) continue;
			if (fileName[extensionStart-1] != '.') continue;
			for (int j = 0; j < extensionLength; j++) {
				if (fileName[extensionStart + j] != extension[j])
					continue extensions;
			}
			return true;
		}
		return false;
	}

	/*
	 * Returns the index of the Java like extension of the given file name
	 * or -1 if it doesn't end with a known Java like extension. 
	 * Note this is the index of the '.' even if it is not considered part of the extension.
	 */
	public static int indexOfJavaLikeExtension(String fileName) {
		int fileNameLength = fileName.length();
		char[][] javaLikeExtensions = Util2.getJavaLikeExtensions();
		extensions: for (int i = 0, length = javaLikeExtensions.length; i < length; i++) {
			char[] extension = javaLikeExtensions[i];
			int extensionLength = extension.length;
			int extensionStart = fileNameLength - extensionLength;
			int dotIndex = extensionStart - 1;
			if (dotIndex < 0) continue;
			if (fileName.charAt(dotIndex) != '.') continue;
			for (int j = 0; j < extensionLength; j++) {
				if (fileName.charAt(extensionStart + j) != extension[j])
					continue extensions;
			}
			return dotIndex;
		}
		return -1;
	}

	/*
	 * Add a log entry
	 */
	public static void log(Throwable e, String message) {
		/*Throwable nestedException;
		if (e instanceof JavaModelException 
				&& (nestedException = ((JavaModelException)e).getException()) != null) {
			e = nestedException;
		}
		IStatus status= new Status(
			IStatus.ERROR, 
			JavaCore.PLUGIN_ID, 
			IStatus.ERROR, 
			message, 
			e); 
		JavaCore.getPlugin().getLog().log(status);
		*/
	}

	/**
	 * Scans the given string for a type signature starting at the given index
	 * and returns the index of the last character.
	 * <pre>
	 * TypeSignature:
	 *  |  BaseTypeSignature
	 *  |  ArrayTypeSignature
	 *  |  ClassTypeSignature
	 *  |  TypeVariableSignature
	 * </pre>
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a type signature
	 */
	public static int scanTypeSignature(char[] string, int start) {
		// need a minimum 1 char
		if (start >= string.length) {
			throw new IllegalArgumentException();
		}
		char c = string[start];
		switch (c) {
			case Signature.C_ARRAY :
				return Util2.scanArrayTypeSignature(string, start);
			case Signature.C_RESOLVED :
			case Signature.C_UNRESOLVED :
				return Util2.scanClassTypeSignature(string, start);
			case Signature.C_TYPE_VARIABLE :
				return Util2.scanTypeVariableSignature(string, start);
			case Signature.C_BOOLEAN :
			case Signature.C_BYTE :
			case Signature.C_CHAR :
			case Signature.C_DOUBLE :
			case Signature.C_FLOAT :
			case Signature.C_INT :
			case Signature.C_LONG :
			case Signature.C_SHORT :
			case Signature.C_VOID :
				return Util2.scanBaseTypeSignature(string, start);
			case Signature.C_CAPTURE :
				return Util2.scanCaptureTypeSignature(string, start);
			case Signature.C_EXTENDS:
			case Signature.C_SUPER:
			case Signature.C_STAR:
				return Util2.scanTypeBoundSignature(string, start);
			default :
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Returns the registered Java like extensions.
	 */
	public static char[][] getJavaLikeExtensions() {
		if (Util2.JAVA_LIKE_EXTENSIONS == null) {
			// TODO (jerome) reenable once JDT UI supports other file extensions (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=71460)
			if (!Util2.ENABLE_JAVA_LIKE_EXTENSIONS)
				Util2.JAVA_LIKE_EXTENSIONS = new char[][] {SuffixConstants.EXTENSION_java.toCharArray()};
			else {
				IContentType javaContentType = Platform.getContentTypeManager().getContentType(JavaCore.JAVA_SOURCE_CONTENT_TYPE);
				HashSet fileExtensions = new HashSet();
				// content types derived from java content type should be included (https://bugs.eclipse.org/bugs/show_bug.cgi?id=121715)
				IContentType[] contentTypes = Platform.getContentTypeManager().getAllContentTypes();
				for (int i = 0, length = contentTypes.length; i < length; i++) {
					if (contentTypes[i].isKindOf(javaContentType)) { // note that javaContentType.isKindOf(javaContentType) == true
						String[] fileExtension = contentTypes[i].getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
						for (int j = 0, length2 = fileExtension.length; j < length2; j++) {
							fileExtensions.add(fileExtension[j]);
						}
					}
				}
				int length = fileExtensions.size();
				// note that file extensions contains "java" as it is defined in JDT Core's plugin.xml
				char[][] extensions = new char[length][];
				extensions[0] = SuffixConstants.EXTENSION_java.toCharArray(); // ensure that "java" is first
				int index = 1;
				Iterator iterator = fileExtensions.iterator();
				while (iterator.hasNext()) {
					String fileExtension = (String) iterator.next();
					if (SuffixConstants.EXTENSION_java.equals(fileExtension))
						continue;
					extensions[index++] = fileExtension.toCharArray();
				}
				Util2.JAVA_LIKE_EXTENSIONS = extensions;
			}
		}
		return Util2.JAVA_LIKE_EXTENSIONS;
	}

	/**
	 * Scans the given string for an array type signature starting at the given
	 * index and returns the index of the last character.
	 * <pre>
	 * ArrayTypeSignature:
	 *     <b>[</b> TypeSignature
	 * </pre>
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not an array type signature
	 */
	public static int scanArrayTypeSignature(char[] string, int start) {
		int length = string.length;
		// need a minimum 2 char
		if (start >= length - 1) {
			throw new IllegalArgumentException();
		}
		char c = string[start];
		if (c != Signature.C_ARRAY) {
			throw new IllegalArgumentException();
		}
		
		c = string[++start];
		while(c == Signature.C_ARRAY) {
			// need a minimum 2 char
			if (start >= length - 1) {
				throw new IllegalArgumentException();
			}
			c = string[++start];
		}
		return Util2.scanTypeSignature(string, start);
	}

	/**
	 * Scans the given string for a class type signature starting at the given
	 * index and returns the index of the last character.
	 * <pre>
	 * ClassTypeSignature:
	 *     { <b>L</b> | <b>Q</b> } Identifier
	 *           { { <b>/</b> | <b>.</b> Identifier [ <b>&lt;</b> TypeArgumentSignature* <b>&gt;</b> ] }
	 *           <b>;</b>
	 * </pre>
	 * Note that although all "/"-identifiers most come before "."-identifiers,
	 * there is no syntactic ambiguity. This method will accept them without
	 * complaint.
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a class type signature
	 */
	public static int scanClassTypeSignature(char[] string, int start) {
		// need a minimum 3 chars "Lx;"
		if (start >= string.length - 2) { 
			throw new IllegalArgumentException();
		}
		// must start in "L" or "Q"
		char c = string[start];
		if (c != Signature.C_RESOLVED && c != Signature.C_UNRESOLVED) {
			return -1;
		}
		int p = start + 1;
		while (true) {
			if (p >= string.length) {
				throw new IllegalArgumentException();
			}
			c = string[p];
			if (c == Signature.C_SEMICOLON) {
				// all done
				return p;
			} else if (c == Signature.C_GENERIC_START) {
				int e = Util2.scanTypeArgumentSignatures(string, p);
				p = e;
			} else if (c == Signature.C_DOT || c == '/') {
				int id = Util2.scanIdentifier(string, p + 1);
				p = id;
			}
			p++;
		}
	}

	/**
	 * Scans the given string for a type variable signature starting at the given
	 * index and returns the index of the last character.
	 * <pre>
	 * TypeVariableSignature:
	 *     <b>T</b> Identifier <b>;</b>
	 * </pre>
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a type variable signature
	 */
	public static int scanTypeVariableSignature(char[] string, int start) {
		// need a minimum 3 chars "Tx;"
		if (start >= string.length - 2) { 
			throw new IllegalArgumentException();
		}
		// must start in "T"
		char c = string[start];
		if (c != Signature.C_TYPE_VARIABLE) {
			throw new IllegalArgumentException();
		}
		int id = Util2.scanIdentifier(string, start + 1);
		c = string[id + 1];
		if (c == Signature.C_SEMICOLON) {
			return id + 1;
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Scans the given string for a base type signature starting at the given index
	 * and returns the index of the last character.
	 * <pre>
	 * BaseTypeSignature:
	 *     <b>B</b> | <b>C</b> | <b>D</b> | <b>F</b> | <b>I</b>
	 *   | <b>J</b> | <b>S</b> | <b>V</b> | <b>Z</b>
	 * </pre>
	 * Note that although the base type "V" is only allowed in method return types,
	 * there is no syntactic ambiguity. This method will accept them anywhere
	 * without complaint.
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a base type signature
	 */
	public static int scanBaseTypeSignature(char[] string, int start) {
		// need a minimum 1 char
		if (start >= string.length) {
			throw new IllegalArgumentException();
		}
		char c = string[start];
		if ("BCDFIJSVZ".indexOf(c) >= 0) { //$NON-NLS-1$
			return start;
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Scans the given string for a type bound signature starting at the given
	 * index and returns the index of the last character.
	 * <pre>
	 * TypeBoundSignature:
	 *     <b>[-+]</b> TypeSignature <b>;</b>
	 *     <b>*</b></b>
	 * </pre>
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a type variable signature
	 */
	public static int scanTypeBoundSignature(char[] string, int start) {
		// need a minimum 1 char for wildcard
		if (start >= string.length) {
			throw new IllegalArgumentException();
		}
		char c = string[start];
		switch (c) {
			case Signature.C_STAR :
				return start;
			case Signature.C_SUPER :
			case Signature.C_EXTENDS :
				// need a minimum 3 chars "+[I"
				if (start >= string.length - 2) {
					throw new IllegalArgumentException();
				}
				break;
			default :
				// must start in "+/-"
					throw new IllegalArgumentException();
				
		}
		c = string[++start];
		switch (c) {
			case Signature.C_CAPTURE :
				return Util2.scanCaptureTypeSignature(string, start);
			case Signature.C_SUPER :
			case Signature.C_EXTENDS :
				return scanTypeBoundSignature(string, start);
			case Signature.C_RESOLVED :
			case Signature.C_UNRESOLVED :
				return scanClassTypeSignature(string, start);
			case Signature.C_TYPE_VARIABLE :
				return scanTypeVariableSignature(string, start);
			case Signature.C_ARRAY :
				return scanArrayTypeSignature(string, start);
			case Signature.C_STAR:
				return start;
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Scans the given string for a capture of a wildcard type signature starting at the given
	 * index and returns the index of the last character.
	 * <pre>
	 * CaptureTypeSignature:
	 *     <b>!</b> TypeBoundSignature
	 * </pre>
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a capture type signature
	 */
	public static int scanCaptureTypeSignature(char[] string, int start) {
		// need a minimum 2 char
		if (start >= string.length - 1) {
			throw new IllegalArgumentException();
		}
		char c = string[start];
		if (c != Signature.C_CAPTURE) {
			throw new IllegalArgumentException();
		}
		return scanTypeBoundSignature(string, start + 1);
	}

	public static char[][] JAVA_LIKE_EXTENSIONS;
	public static boolean ENABLE_JAVA_LIKE_EXTENSIONS = true;
	public static final String EMPTY_ARGUMENT = "   "; //$NON-NLS-1$
	public static final String ARGUMENTS_DELIMITER = "#"; //$NON-NLS-1$
	/**
	 * Scans the given string for a list of type argument signatures starting at
	 * the given index and returns the index of the last character.
	 * <pre>
	 * TypeArgumentSignatures:
	 *     <b>&lt;</b> TypeArgumentSignature* <b>&gt;</b>
	 * </pre>
	 * Note that although there is supposed to be at least one type argument, there
	 * is no syntactic ambiguity if there are none. This method will accept zero
	 * type argument signatures without complaint.
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a list of type arguments
	 * signatures
	 */
	public static int scanTypeArgumentSignatures(char[] string, int start) {
		// need a minimum 2 char "<>"
		if (start >= string.length - 1) {
			throw new IllegalArgumentException();
		}
		char c = string[start];
		if (c != Signature.C_GENERIC_START) {
			throw new IllegalArgumentException();
		}
		int p = start + 1;
		while (true) {
			if (p >= string.length) {
				throw new IllegalArgumentException();
			}
			c = string[p];
			if (c == Signature.C_GENERIC_END) {
				return p;
			}
			int e = Util2.scanTypeArgumentSignature(string, p);
			p = e + 1;
		}
	}

	/**
	 * Scans the given string for an identifier starting at the given
	 * index and returns the index of the last character. 
	 * Stop characters are: ";", ":", "&lt;", "&gt;", "/", ".".
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not an identifier
	 */
	public static int scanIdentifier(char[] string, int start) {
		// need a minimum 1 char
		if (start >= string.length) { 
			throw new IllegalArgumentException();
		}
		int p = start;
		while (true) {
			char c = string[p];
			if (c == '<' || c == '>' || c == ':' || c == ';' || c == '.' || c == '/') {
				return p - 1;
			}
			p++;
			if (p == string.length) {
				return p - 1;
			}
		}
	}

	/**
	 * Scans the given string for a type argument signature starting at the given
	 * index and returns the index of the last character.
	 * <pre>
	 * TypeArgumentSignature:
	 *     <b>&#42;</b>
	 *  |  <b>+</b> TypeSignature
	 *  |  <b>-</b> TypeSignature
	 *  |  TypeSignature
	 * </pre>
	 * Note that although base types are not allowed in type arguments, there is
	 * no syntactic ambiguity. This method will accept them without complaint.
	 * 
	 * @param string the signature string
	 * @param start the 0-based character index of the first character
	 * @return the 0-based character index of the last character
	 * @exception IllegalArgumentException if this is not a type argument signature
	 */
	public static int scanTypeArgumentSignature(char[] string, int start) {
		// need a minimum 1 char
		if (start >= string.length) {
			throw new IllegalArgumentException();
		}
		char c = string[start];
		switch (c) {
			case Signature.C_STAR :
				return start;
			case Signature.C_EXTENDS :
			case Signature.C_SUPER :
				return Util2.scanTypeBoundSignature(string, start);
			default :
				return scanTypeSignature(string, start);
		}
	}


}
