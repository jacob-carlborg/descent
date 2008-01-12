/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Corporation - added J2SE 1.5 support
 *******************************************************************************/
package descent.core;

import java.util.ArrayList;
import java.util.Stack;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.ScannerHelper;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.core.SignatureProcessor;
import descent.internal.core.SignatureRequestorAdapter;
import descent.internal.core.util.Util;


/**
 * Provides methods for encoding and decoding type and method signature strings.
 * <p>
 * Signatures obtained from parsing source files (i.e. files with one of the 
 * {@link JavaCore#getJavaLikeExtensions() Java-like extensions}) differ subtly 
 * from ones obtained from pre-compiled binary (".class") files in class names are
 * usually left unresolved in the former. For example, the normal resolved form
 * of the type "String" embeds the class's package name ("Ljava.lang.String;"
 * or "Ljava/lang/String;"), whereas the unresolved form contains only what is
 * written "QString;".
 * </p>
 * <p>
 * Generic types introduce to the Java language in J2SE 1.5 add three new
 * facets to signatures: type variables, parameterized types with type arguments,
 * and formal type parameters. <i>Rich</i> signatures containing these facets
 * only occur when dealing with code that makes overt use of the new language
 * features. All other code, and certainly all Java code written or compiled
 * with J2SE 1.4 or earlier, involved only <i>simple</i> signatures.
 * </p>
 * <p>
 * Note that the "Q" and "!" formats are specific to Eclipse; the remainder
 * are specified in the JVM spec.
 * </p>
 * <p>
 * The syntax for a type signature is:
 * <pre>
 * TypeSignature ::=
 *     "B"  // byte
 *   | "C"  // char
 *   | "D"  // double
 *   | "F"  // float
 *   | "I"  // int
 *   | "J"  // long
 *   | "S"  // short
 *   | "V"  // void
 *   | "Z"  // boolean
 *   | "T" + Identifier + ";" // type variable
 *   | "[" + TypeSignature  // array X[]
 *   | "!" + TypeSignature  // capture-of ?
 *   | ResolvedClassTypeSignature
 *   | UnresolvedClassTypeSignature
 * 
 * ResolvedClassTypeSignature ::= // resolved named type (in compiled code)
 *     "L" + Identifier + OptionalTypeArguments
 *           ( ( "." | "/" ) + Identifier + OptionalTypeArguments )* + ";"
 * 
 * UnresolvedClassTypeSignature ::= // unresolved named type (in source code)
 *     "Q" + Identifier + OptionalTypeArguments
 *           ( ( "." | "/" ) + Identifier + OptionalTypeArguments )* + ";"
 * 
 * OptionalTypeArguments ::=
 *     "&lt;" + TypeArgument+ + "&gt;" 
 *   |
 * 
 * TypeArgument ::=
 *   | TypeSignature
 *   | "*" // wildcard ?
 *   | "+" TypeSignature // wildcard ? extends X
 *   | "-" TypeSignature // wildcard ? super X
 * </pre>
 * </p>
 * <p>
 * Examples:
 * <ul>
 *   <li><code>"[[I"</code> denotes <code>int[][]</code></li>
 *   <li><code>"Ljava.lang.String;"</code> denotes <code>java.lang.String</code> in compiled code</li>
 *   <li><code>"QString;"</code> denotes <code>String</code> in source code</li>
 *   <li><code>"Qjava.lang.String;"</code> denotes <code>java.lang.String</code> in source code</li>
 *   <li><code>"[QString;"</code> denotes <code>String[]</code> in source code</li>
 *   <li><code>"QMap&lt;QString;*&gt;;"</code> denotes <code>Map&lt;String,?&gt;</code> in source code</li>
 *   <li><code>"Qjava.util.List&ltTV;&gt;;"</code> denotes <code>java.util.List&lt;V&gt;</code> in source code</li>
 * </ul>
 * </p>
 * <p>
 * The syntax for a method signature is: 
 * <pre>
 * MethodSignature ::= "(" + ParamTypeSignature* + ")" + ReturnTypeSignature
 * ParamTypeSignature ::= TypeSignature
 * ReturnTypeSignature ::= TypeSignature
 * </pre>
 * <p>
 * Examples:
 * <ul>
 *   <li><code>"()I"</code> denotes <code>int foo()</code></li>
 *   <li><code>"([Ljava.lang.String;)V"</code> denotes <code>void foo(java.lang.String[])</code> in compiled code</li>
 *   <li><code>"(QString;)QObject;"</code> denotes <code>Object foo(String)</code> in source code</li>
 * </ul>
 * </p>
 * <p>
 * The syntax for a formal type parameter signature is:
 * <pre>
 * FormalTypeParameterSignature ::=
 *     TypeVariableName + OptionalClassBound + InterfaceBound*
 * TypeVariableName ::= Identifier
 * OptionalClassBound ::=
 *     ":"
 *   | ":" + TypeSignature
 * InterfaceBound ::= 
 *     ":" + TypeSignature
 * </pre>
 * <p>
 * Examples:
 * <ul>
 *   <li><code>"X:"</code> denotes <code>X</code></li>
 *   <li><code>"X:QReader;"</code> denotes <code>X extends Reader</code> in source code</li>
 *   <li><code>"X:QReader;:QSerializable;"</code> denotes <code>X extends Reader & Serializable</code> in source code</li>
 * </ul>
 * </p>
 * <p>
 * This class provides static methods and constants only; it is not intended to be
 * instantiated or subclassed by clients.
 * </p>
 */
public final class Signature {

	/**
	 * Character constant indicating the primitive type boolean in a signature.
	 * Value is <code>'Z'</code>.
	 */
	public static final char C_BOOLEAN 		= 'Z';

	/**
	 * Character constant indicating the primitive type byte in a signature.
	 * Value is <code>'B'</code>.
	 */
	public static final char C_BYTE 		= 'B';

	/**
	 * Character constant indicating the primitive type char in a signature.
	 * Value is <code>'C'</code>.
	 */
	public static final char C_CHAR 		= 'C';

	/**
	 * Character constant indicating the primitive type double in a signature.
	 * Value is <code>'D'</code>.
	 */
	public static final char C_DOUBLE 		= 'D';

	/**
	 * Character constant indicating the primitive type float in a signature.
	 * Value is <code>'F'</code>.
	 */
	public static final char C_FLOAT 		= 'F';

	/**
	 * Character constant indicating the primitive type int in a signature.
	 * Value is <code>'I'</code>.
	 */
	public static final char C_INT 			= 'I';
	
	/**
	 * Character constant indicating the semicolon in a signature.
	 * Value is <code>';'</code>.
	 */
	public static final char C_SEMICOLON 			= ';';

	/**
	 * Character constant indicating the colon in a signature.
	 * Value is <code>':'</code>.
	 * @since 3.0
	 */
	public static final char C_COLON 			= ':';

	/**
	 * Character constant indicating the primitive type long in a signature.
	 * Value is <code>'J'</code>.
	 */
	public static final char C_LONG			= 'J';
	
	/**
	 * Character constant indicating the primitive type short in a signature.
	 * Value is <code>'S'</code>.
	 */
	public static final char C_SHORT		= 'S';
	
	/**
	 * Character constant indicating result type void in a signature.
	 * Value is <code>'V'</code>.
	 */
	public static final char C_VOID			= 'V';
	
	/**
	 * Character constant indicating the start of a resolved type variable in a 
	 * signature. Value is <code>'T'</code>.
	 * @since 3.0
	 */
	public static final char C_TYPE_VARIABLE	= 'T';
	
	/**
	 * Character constant indicating an unbound wildcard type argument 
	 * in a signature.
	 * Value is <code>'*'</code>.
	 * @since 3.0
	 */
	public static final char C_STAR	= '*';
	
	/**
	 * Character constant indicating an exception in a signature.
	 * Value is <code>'^'</code>.
	 * @since 3.1
	 */
	public static final char C_EXCEPTION_START	= '^';
	
	/**
	 * Character constant indicating a bound wildcard type argument 
	 * in a signature with extends clause.
	 * Value is <code>'+'</code>.
	 * @since 3.1
	 */
	public static final char C_EXTENDS	= '+';

	/**
	 * Character constant indicating a bound wildcard type argument 
	 * in a signature with super clause.
	 * Value is <code>'-'</code>.
	 * @since 3.1
	 */
	public static final char C_SUPER	= '-';
	
	/** 
	 * Character constant indicating the dot in a signature. 
	 * Value is <code>'.'</code>.
	 */
	public static final char C_DOT			= '.';
	
	/** 
	 * Character constant indicating the dollar in a signature.
	 * Value is <code>'$'</code>.
	 */
	public static final char C_DOLLAR			= '$';

	/** 
	 * Character constant indicating an array type in a signature.
	 * Value is <code>'['</code>.
	 */
	public static final char C_ARRAY		= '[';

	/** 
	 * Character constant indicating the start of a resolved, named type in a 
	 * signature. Value is <code>'L'</code>.
	 */
	public static final char C_RESOLVED		= 'L';

	/** 
	 * Character constant indicating the start of an unresolved, named type in a
	 * signature. Value is <code>'Q'</code>.
	 */
	public static final char C_UNRESOLVED	= 'Q';

	/**
	 * Character constant indicating the end of a named type in a signature. 
	 * Value is <code>';'</code>.
	 */
	public static final char C_NAME_END		= ';';

	/**
	 * Character constant indicating the start of a parameter type list in a
	 * signature. Value is <code>'('</code>.
	 */
	public static final char C_PARAM_START	= '(';

	/**
	 * Character constant indicating the end of a parameter type list in a 
	 * signature. Value is <code>')'</code>.
	 */
	public static final char C_PARAM_END	= ')';

	/**
	 * Character constant indicating the start of a formal type parameter
	 * (or type argument) list in a signature. Value is <code>'&lt;'</code>.
	 * @since 3.0
	 */
	public static final char C_GENERIC_START	= '<';

	/**
	 * Character constant indicating the end of a generic type list in a 
	 * signature. Value is <code>'&gt;'</code>.
	 * @since 3.0
	 */
	public static final char C_GENERIC_END	= '>';

	/**
	 * Character constant indicating a capture of a wildcard type in a 
	 * signature. Value is <code>'!'</code>.
	 * @since 3.1
	 */
	public static final char C_CAPTURE	= '!';
	
	/**
	 * String constant for the signature of the primitive type boolean.
	 * Value is <code>"Z"</code>.
	 */
	public static final String SIG_BOOLEAN 		= "Z"; //$NON-NLS-1$

	/**
	 * String constant for the signature of the primitive type byte. 
	 * Value is <code>"B"</code>.
	 */
	public static final String SIG_BYTE 		= "B"; //$NON-NLS-1$

	/**
	 * String constant for the signature of the primitive type char.
	 * Value is <code>"C"</code>.
	 */
	public static final String SIG_CHAR 		= "C"; //$NON-NLS-1$

	/**
	 * String constant for the signature of the primitive type double.
	 * Value is <code>"D"</code>.
	 */
	public static final String SIG_DOUBLE 		= "D"; //$NON-NLS-1$

	/**
	 * String constant for the signature of the primitive type float.
	 * Value is <code>"F"</code>.
	 */
	public static final String SIG_FLOAT 		= "F"; //$NON-NLS-1$

	/**
	 * String constant for the signature of the primitive type int.
	 * Value is <code>"I"</code>.
	 */
	public static final String SIG_INT 			= "I"; //$NON-NLS-1$

	/**
	 * String constant for the signature of the primitive type long.
	 * Value is <code>"J"</code>.
	 */
	public static final String SIG_LONG			= "J"; //$NON-NLS-1$

	/**
	 * String constant for the signature of the primitive type short.
	 * Value is <code>"S"</code>.
	 */
	public static final String SIG_SHORT		= "S"; //$NON-NLS-1$

	/** String constant for the signature of result type void.
	 * Value is <code>"V"</code>.
	 */
	public static final String SIG_VOID			= "V"; //$NON-NLS-1$
	

	/**
	 * Kind constant for a class type signature.
	 * @see #getTypeSignatureKind(String)
	 * @since 3.0
	 */
	public static final int CLASS_TYPE_SIGNATURE = 1;

	/**
	 * Kind constant for a base (primitive or void) type signature.
	 * @see #getTypeSignatureKind(String)
	 * @since 3.0
	 */
	public static final int BASE_TYPE_SIGNATURE = 2;

	/**
	 * Kind constant for a type variable signature.
	 * @see #getTypeSignatureKind(String)
	 * @since 3.0
	 */
	public static final int TYPE_VARIABLE_SIGNATURE = 3;

	/**
	 * Kind constant for an array type signature.
	 * @see #getTypeSignatureKind(String)
	 * @since 3.0
	 */
	public static final int ARRAY_TYPE_SIGNATURE = 4;
	
	/**
	 * Kind constant for a wildcard type signature.
	 * @see #getTypeSignatureKind(String)
	 * @since 3.1
	 */
	public static final int WILDCARD_TYPE_SIGNATURE = 5;

	/**
	 * Kind constant for the capture of a wildcard type signature.
	 * @see #getTypeSignatureKind(String)
	 * @since 3.1
	 */
	public static final int CAPTURE_TYPE_SIGNATURE = 6;
	
	/**
	 * Kind constant for an interface type signature.
	 * @see #getTypeSignatureKind(String)
	 * @since 3.0
	 */
	public static final int INTERFACE_TYPE_SIGNATURE = 7;
	
	/**
	 * Kind constant for a struct type signature.
	 * @see #getTypeSignatureKind(String)
	 * @since 3.0
	 */
	public static final int STRUCT_TYPE_SIGNATURE = 8;
	
	/**
	 * Kind constant for a union type signature.
	 * @see #getTypeSignatureKind(String)
	 * @since 3.0
	 */
	public static final int UNION_TYPE_SIGNATURE = 9;
	
	/**
	 * Kind constant for an enum signature.
	 * @see #getTypeSignatureKind(String)
	 * @since 3.0
	 */
	public static final int ENUM_TYPE_SIGNATURE = 10;

	private static final char[] BOOLEAN = "boolean".toCharArray(); //$NON-NLS-1$
	private static final char[] BYTE = "byte".toCharArray(); //$NON-NLS-1$
	private static final char[] CHAR = "char".toCharArray(); //$NON-NLS-1$
	private static final char[] DOUBLE = "double".toCharArray(); //$NON-NLS-1$
	private static final char[] FLOAT = "float".toCharArray(); //$NON-NLS-1$
	private static final char[] INT = "int".toCharArray(); //$NON-NLS-1$
	private static final char[] LONG = "long".toCharArray(); //$NON-NLS-1$
	private static final char[] SHORT = "short".toCharArray(); //$NON-NLS-1$
	private static final char[] VOID = "void".toCharArray(); //$NON-NLS-1$
	private static final char[] EXTENDS = "extends".toCharArray(); //$NON-NLS-1$
	private static final char[] SUPER = "super".toCharArray(); //$NON-NLS-1$
	private static final char[] CAPTURE = "capture-of".toCharArray(); //$NON-NLS-1$
	
	private static final String EMPTY = new String(CharOperation.NO_CHAR);
		
private Signature() {
	// Not instantiable
}

private static int checkName(char[] name, char[] typeName, int pos, int length) {
    if (CharOperation.fragmentEquals(name, typeName, pos, true)) {
        pos += name.length;
        if (pos == length) return pos;
        char currentChar = typeName[pos];
        switch (currentChar) {
            case ' ' :
            case '.' :
            case '<' :
            case '>' :
            case '[' :
            case ',' :
                return pos;
			default:
			    if (ScannerHelper.isWhitespace(currentChar))
			    	return pos;
			    
        }
    }
    return -1;
}

/**
 * Creates a new type signature with the given amount of array nesting added 
 * to the given type signature.
 *
 * @param typeSignature the type signature
 * @param arrayCount the desired number of levels of array nesting
 * @return the encoded array type signature
 * 
 * @since 2.0
 */
public static char[] createArraySignature(char[] typeSignature, int arrayCount) {
	if (arrayCount == 0) return typeSignature;
	int sigLength = typeSignature.length;
	char[] result = new char[arrayCount + sigLength];
	for (int i = 0; i < arrayCount; i++) {
		result[i] = C_ARRAY;
	}
	System.arraycopy(typeSignature, 0, result, arrayCount, sigLength);
	return result;
}
/**
 * Creates a new type signature with the given amount of array nesting added 
 * to the given type signature.
 *
 * @param typeSignature the type signature
 * @param arrayCount the desired number of levels of array nesting
 * @return the encoded array type signature
 */
public static String createArraySignature(String typeSignature, int arrayCount) {
	return new String(createArraySignature(typeSignature.toCharArray(), arrayCount));
}

/**
 * Creates a method signature from the given parameter and return type 
 * signatures. The encoded method signature is dot-based.
 *
 * @param parameterTypes the list of parameter type signatures
 * @param returnType the return type signature
 * @return the encoded method signature
 * 
 * @since 2.0
 */
public static char[] createMethodSignature(char[][] parameterTypes, char[] returnType) {
	int parameterTypesLength = parameterTypes.length;
	int parameterLength = 0;
	for (int i = 0; i < parameterTypesLength; i++) {
		parameterLength += parameterTypes[i].length;
		
	}
	int returnTypeLength = returnType.length;
	char[] result = new char[1 + parameterLength + 1 + returnTypeLength];
	result[0] = C_PARAM_START;
	int index = 1;
	for (int i = 0; i < parameterTypesLength; i++) {
		char[] parameterType = parameterTypes[i];
		int length = parameterType.length;
		System.arraycopy(parameterType, 0, result, index, length);
		index += length;
	}
	result[index] = C_PARAM_END;
	System.arraycopy(returnType, 0, result, index+1, returnTypeLength);
	return result;
}

/**
 * Creates a method signature from the given parameter and return type 
 * signatures. The encoded method signature is dot-based. This method
 * is equivalent to
 * <code>createMethodSignature(parameterTypes, returnType)</code>.
 *
 * @param parameterTypes the list of parameter type signatures
 * @param returnType the return type signature
 * @return the encoded method signature
 * @see Signature#createMethodSignature(char[][], char[])
 */
public static String createMethodSignature(String[] parameterTypes, String returnType) {
	int parameterTypesLenth = parameterTypes.length;
	char[][] parameters = new char[parameterTypesLenth][];
	for (int i = 0; i < parameterTypesLenth; i++) {
		parameters[i] = parameterTypes[i].toCharArray();
	}
	return new String(createMethodSignature(parameters, returnType.toCharArray()));
}

/**
 * Creates a new type parameter signature with the given name and bounds.
 *
 * @param typeParameterName the type parameter name
 * @param boundSignatures the signatures of associated bounds or empty array if none
 * @return the encoded type parameter signature
 * 
 * @since 3.1
 */
public static char[] createTypeParameterSignature(char[] typeParameterName, char[][] boundSignatures) {
	int length = boundSignatures.length;
	if (length == 0) {
		return CharOperation.append(typeParameterName, C_COLON); // param signature with no bounds still gets trailing colon
	}
	int boundsSize = 0;
	for (int i = 0; i < length; i++) {
		boundsSize += boundSignatures[i].length + 1;
	}
	int nameLength = typeParameterName.length;
	char[] result = new char[nameLength + boundsSize];
	System.arraycopy(typeParameterName, 0, result, 0, nameLength);
	int index = nameLength;
	for (int i = 0; i < length; i++) {
		result[index++] = C_COLON;
		int boundLength = boundSignatures[i].length;
		System.arraycopy(boundSignatures[i], 0, result, index, boundLength);
		index += boundLength;
	}
	return result;
}

/**
 * Creates a new type parameter signature with the given name and bounds.
 *
 * @param typeParameterName the type parameter name
 * @param boundSignatures the signatures of associated bounds or empty array if none
 * @return the encoded type parameter signature
 * 
 * @since 3.1
 */
public static String createTypeParameterSignature(String typeParameterName, String[] boundSignatures) {
	int length = boundSignatures.length;
	char[][] boundSignatureChars = new char[length][];
	for (int i = 0; i < length; i++) {
		boundSignatureChars[i] = boundSignatures[i].toCharArray();
	}
	return new String(createTypeParameterSignature(typeParameterName.toCharArray(), boundSignatureChars));
}

/**
 * Creates a new type signature from the given type name encoded as a character
 * array. The type name may contain primitive types, array types or parameterized types.
 * This method is equivalent to
 * <code>createTypeSignature(new String(typeName),isResolved)</code>, although
 * more efficient for callers with character arrays rather than strings. If the 
 * type name is qualified, then it is expected to be dot-based.
 *
 * @param typeName the possibly qualified type name
 * @param isResolved <code>true</code> if the type name is to be considered
 *   resolved (for example, a type name from a binary class file), and 
 *   <code>false</code> if the type name is to be considered unresolved
 *   (for example, a type name found in source code)
 * @return the encoded type signature
 * @see #createTypeSignature(java.lang.String,boolean)
 */
public static String createTypeSignature(char[] typeName, boolean isResolved) {
	return new String(createCharArrayTypeSignature(typeName, isResolved));
}

/**
 * Creates a new type signature from the given type name encoded as a character
 * array. The type name may contain primitive types or array types or parameterized types.
 * This method is equivalent to
 * <code>createTypeSignature(new String(typeName),isResolved).toCharArray()</code>,
 * although more efficient for callers with character arrays rather than strings.
 * If the type name is qualified, then it is expected to be dot-based.
 *
 * @param typeName the possibly qualified type name
 * @param isResolved <code>true</code> if the type name is to be considered
 *   resolved (for example, a type name from a binary class file), and 
 *   <code>false</code> if the type name is to be considered unresolved
 *   (for example, a type name found in source code)
 * @return the encoded type signature
 * @see #createTypeSignature(java.lang.String,boolean)
 * 
 * @since 2.0
 */
public static char[] createCharArrayTypeSignature(char[] typeName, boolean isResolved) {
	try {
		if (typeName == null) throw new IllegalArgumentException("null"); //$NON-NLS-1$
		int length = typeName.length;
		if (length == 0) throw new IllegalArgumentException(new String(typeName));
		StringBuffer buffer = new StringBuffer(5);
		int pos = encodeTypeSignature(typeName, 0, isResolved, length, buffer);
		pos = consumeWhitespace(typeName, pos, length);
		if (pos < length) {
			/* TODO JDT UI signature
			throw new IllegalArgumentException(new String(typeName));
			*/
			return "I".toCharArray();
		}
		char[] result = new char[length = buffer.length()];
		buffer.getChars(0, length, result, 0);
		return result;
	} catch (Throwable t) {
		// TODO JDT signature: this is not working some times...
		return "I".toCharArray();
	}
	
}
private static int consumeWhitespace(char[] typeName, int pos, int length) {
    while (pos < length) {
        char currentChar = typeName[pos];
        if (currentChar != ' ' && !CharOperation.isWhitespace(currentChar)) {
            break;
        }
        pos++;
    }
    return pos;
}
private static int encodeQualifiedName(char[] typeName, int pos, int length, StringBuffer buffer) {
    int count = 0;
    char lastAppendedChar = 0;
    nameLoop: while (pos < length) {
	    char currentChar = typeName[pos];
		switch (currentChar) {
		    case '<' :
		    case '>' :
		    case '[' :
		    case ',' :
		        break nameLoop;
			case '.' :
			    buffer.append(C_DOT);
				lastAppendedChar = C_DOT;
			    count++;
			    break;
			default:
			    if (currentChar == ' ' || ScannerHelper.isWhitespace(currentChar)) {
			        if (lastAppendedChar == C_DOT) { // allow spaces after a dot
			            pos = consumeWhitespace(typeName, pos, length) - 1; // will be incremented
			            break; 
			        }
			        // allow spaces before a dot
				    int checkPos = checkNextChar(typeName, '.', pos, length, true);
				    if (checkPos > 0) {
				        buffer.append(C_DOT);			// process dot immediately to avoid one iteration
				        lastAppendedChar = C_DOT;
				        count++;
				        pos = checkPos;
				        break;
				    }
				    break nameLoop;
			    }
			    buffer.append(currentChar);
			    lastAppendedChar = currentChar;
				count++;
			    break;
		}
	    pos++;
    }
    if (count == 0) throw new IllegalArgumentException(new String(typeName));
	return pos;
}

private static int encodeArrayDimension(char[] typeName, int pos, int length, StringBuffer buffer) {
    int checkPos;
    while (pos < length && (checkPos = checkNextChar(typeName, '[', pos, length, true)) > 0) {
        pos = checkNextChar(typeName, ']', checkPos, length, false);
        buffer.append(C_ARRAY);
    }
    return pos;
}
private static int checkArrayDimension(char[] typeName, int pos, int length) {
    int genericBalance = 0;
    while (pos < length) {
		switch(typeName[pos]) {
		    case '<' :
		        genericBalance++;
		        break;
		    case ',' :
			    if (genericBalance == 0) return -1;
			    break;
			case '>':
			    if (genericBalance == 0) return -1;
			    genericBalance--;
		        break;
			case '[':
			    if (genericBalance == 0) {
			        return pos;
			    }
		}
		pos++;
    }
    return -1;
}
private static int checkNextChar(char[] typeName, char expectedChar, int pos, int length, boolean isOptional) {
    pos = consumeWhitespace(typeName, pos, length);
    if (pos < length && typeName[pos] == expectedChar) 
        return pos + 1;
    if (!isOptional) throw new IllegalArgumentException(new String(typeName));
    return -1;
}

private static int encodeTypeSignature(char[] typeName, int start, boolean isResolved, int length, StringBuffer buffer) {
    int pos = start;
    pos = consumeWhitespace(typeName, pos, length);
    if (pos >= length) throw new IllegalArgumentException(new String(typeName));
    int checkPos;
    char currentChar = typeName[pos];
    switch (currentChar) {
		// primitive type?
		case 'b' :
		    checkPos = checkName(BOOLEAN, typeName, pos, length);
		    if (checkPos > 0) {
		        pos = encodeArrayDimension(typeName, checkPos, length, buffer);
			    buffer.append(C_BOOLEAN);
			    return pos;
			} 
		    checkPos = checkName(BYTE, typeName, pos, length);
		    if (checkPos > 0) {
		        pos = encodeArrayDimension(typeName, checkPos, length, buffer);
			    buffer.append(C_BYTE);
			    return pos;
			}
		    break;
		case 'd':
		    checkPos = checkName(DOUBLE, typeName, pos, length);
		    if (checkPos > 0) {
		        pos = encodeArrayDimension(typeName, checkPos, length, buffer);
			    buffer.append(C_DOUBLE);
			    return pos;
			} 
		    break;
		case 'f':
		    checkPos = checkName(FLOAT, typeName, pos, length);
		    if (checkPos > 0) {
		        pos = encodeArrayDimension(typeName, checkPos, length, buffer);
			    buffer.append(C_FLOAT);
			    return pos;
			} 
		    break;
		case 'i':
		    checkPos = checkName(INT, typeName, pos, length);
		    if (checkPos > 0) {
		        pos = encodeArrayDimension(typeName, checkPos, length, buffer);
			    buffer.append(C_INT);
			    return pos;
			} 
		    break;
		case 'l':
		    checkPos = checkName(LONG, typeName, pos, length);
		    if (checkPos > 0) {
		        pos = encodeArrayDimension(typeName, checkPos, length, buffer);
			    buffer.append(C_LONG);
			    return pos;
			} 
		    break;
		case 's':
		    checkPos = checkName(SHORT, typeName, pos, length);
		    if (checkPos > 0) {
		        pos = encodeArrayDimension(typeName, checkPos, length, buffer);
			    buffer.append(C_SHORT);
			    return pos;
			} 
		    break;
		case 'v':
		    checkPos = checkName(VOID, typeName, pos, length);
		    if (checkPos > 0) {
		        pos = encodeArrayDimension(typeName, checkPos, length, buffer);
			    buffer.append(C_VOID);
			    return pos;
			}
		    break;
		case 'c':
		    checkPos = checkName(CHAR, typeName, pos, length);
		    if (checkPos > 0) {
		        pos = encodeArrayDimension(typeName, checkPos, length, buffer);
			    buffer.append(C_CHAR);
			    return pos;
			} else {
				checkPos = checkName(CAPTURE, typeName, pos, length);
				if (checkPos > 0) {
					pos = consumeWhitespace(typeName, checkPos, length);
					if (typeName[pos] != '?') {
						break;
					}
				} else {
					break;
				}
			}
			buffer.append(C_CAPTURE);
			// fall-thru for wildcard part of capture typecheckPos
		case '?':
			// wildcard
			pos = consumeWhitespace(typeName, pos+1, length);
			checkPos = checkName(EXTENDS, typeName, pos, length);
			if (checkPos > 0) {
				buffer.append(C_EXTENDS);
				pos = encodeTypeSignature(typeName, checkPos, isResolved, length, buffer);
				return pos;
			}
			checkPos = checkName(SUPER, typeName, pos, length);
			if (checkPos > 0) {
				buffer.append(C_SUPER);
				pos = encodeTypeSignature(typeName, checkPos, isResolved, length, buffer);
				return pos;
			}
			buffer.append(C_STAR);
			return pos;
    }		    
    // non primitive type
    checkPos = checkArrayDimension(typeName, pos, length);
	int end;
	if (checkPos > 0) {
	    end = encodeArrayDimension(typeName, checkPos, length, buffer);
	} else {
	    end = -1;
	}
	buffer.append(isResolved ? C_RESOLVED : C_UNRESOLVED);
	while (true) { // loop on qualifiedName[<args>][.qualifiedName[<args>]*
	    pos = encodeQualifiedName(typeName, pos, length, buffer);
		checkPos = checkNextChar(typeName, '<', pos, length, true);
		if (checkPos > 0) {
			buffer.append(C_GENERIC_START);
		    pos = encodeTypeSignature(typeName, checkPos, isResolved, length, buffer);
		    while ((checkPos = checkNextChar(typeName, ',', pos, length, true)) > 0) {
			    pos = encodeTypeSignature(typeName, checkPos, isResolved, length, buffer);
		    }
		    pos = checkNextChar(typeName, '>', pos, length, false);
			buffer.append(C_GENERIC_END);
		}
		checkPos = checkNextChar(typeName, '.', pos, length, true);
		if (checkPos > 0) {
			buffer.append(C_DOT);
			pos = checkPos;
		} else {
			break;
		}
	}
	buffer.append(C_NAME_END);
	if (end > 0) pos = end; // skip array dimension which were preprocessed
    return pos;
}

/**
 * Creates a new type signature from the given type name. If the type name is qualified,
 * then it is expected to be dot-based. The type name may contain primitive
 * types or array types. However, parameterized types are not supported.
 * <p>
 * For example:
 * <pre>
 * <code>
 * createTypeSignature("int", hucairz) -> "I"
 * createTypeSignature("java.lang.String", true) -> "Ljava.lang.String;"
 * createTypeSignature("String", false) -> "QString;"
 * createTypeSignature("java.lang.String", false) -> "Qjava.lang.String;"
 * createTypeSignature("int []", false) -> "[I"
 * </code>
 * </pre>
 * </p>
 *
 * @param typeName the possibly qualified type name
 * @param isResolved <code>true</code> if the type name is to be considered
 *   resolved (for example, a type name from a binary class file), and 
 *   <code>false</code> if the type name is to be considered unresolved
 *   (for example, a type name found in source code)
 * @return the encoded type signature
 */
public static String createTypeSignature(String typeName, boolean isResolved) {
	return createTypeSignature(typeName == null ? null : typeName.toCharArray(), isResolved);
}

/**
 * Returns the array count (array nesting depth) of the given type signature.
 *
 * @param typeSignature the type signature
 * @return the array nesting depth, or 0 if not an array
 * @exception IllegalArgumentException if the signature is not syntactically
 *   correct
 * 
 * @since 2.0
 */
public static int getArrayCount(char[] typeSignature) throws IllegalArgumentException {	
	try {
		int count = 0;
		while (typeSignature[count] == C_ARRAY) {
			++count;
		}
		return count;
	} catch (ArrayIndexOutOfBoundsException e) { // signature is syntactically incorrect if last character is C_ARRAY
		throw new IllegalArgumentException();
	}
}
/**
 * Returns the array count (array nesting depth) of the given type signature.
 *
 * @param typeSignature the type signature
 * @return the array nesting depth, or 0 if not an array
 * @exception IllegalArgumentException if the signature is not syntactically
 *   correct
 */
public static int getArrayCount(String typeSignature) throws IllegalArgumentException {
	return getArrayCount(typeSignature.toCharArray());
}
/**
 * Returns the type signature without any array nesting.
 * <p>
 * For example:
 * <pre>
 * <code>
 * getElementType({'[', '[', 'I'}) --> {'I'}.
 * </code>
 * </pre>
 * </p>
 * 
 * @param typeSignature the type signature
 * @return the type signature without arrays
 * @exception IllegalArgumentException if the signature is not syntactically
 *   correct
 * 
 * @since 2.0
 */
public static char[] getElementType(char[] typeSignature) throws IllegalArgumentException {
	int count = getArrayCount(typeSignature);
	if (count == 0) return typeSignature;
	int length = typeSignature.length;
	char[] result = new char[length-count];
	System.arraycopy(typeSignature, count, result, 0, length-count);
	return result;
}
/**
 * Returns the type signature without any array nesting.
 * <p>
 * For example:
 * <pre>
 * <code>
 * getElementType("[[I") --> "I".
 * </code>
 * </pre>
 * </p>
 * 
 * @param typeSignature the type signature
 * @return the type signature without arrays
 * @exception IllegalArgumentException if the signature is not syntactically
 *   correct
 */
public static String getElementType(String typeSignature) throws IllegalArgumentException {
	return new String(getElementType(typeSignature.toCharArray()));
}
/**
 * Returns the number of parameter types in the given method signature.
 *
 * @param methodSignature the method signature
 * @return the number of parameters
 * @exception IllegalArgumentException if the signature is not syntactically
 *   correct
 * @since 2.0
 */
public static int getParameterCount(char[] methodSignature) throws IllegalArgumentException {
	return getParameterCount(new String(methodSignature));
}

/**
 * Returns the kind of type signature encoded by the given string.
 * 
 * @param typeSignature the type signature string
 * @return the kind of type signature; one of the kind constants:
 * {@link #ARRAY_TYPE_SIGNATURE}, {@link #CLASS_TYPE_SIGNATURE},
 * {@link #BASE_TYPE_SIGNATURE}, or {@link #TYPE_VARIABLE_SIGNATURE},
 * or (since 3.1) {@link #WILDCARD_TYPE_SIGNATURE} or {@link #CAPTURE_TYPE_SIGNATURE}
 * @exception IllegalArgumentException if this is not a type signature
 * @since 3.0
 */
public static int getTypeSignatureKind(char[] typeSignature) {
	return getTypeSignatureKind(new String(typeSignature));
}

/**
 * Returns the kind of type signature encoded by the given string.
 * 
 * @param typeSignature the type signature string
 * @return the kind of type signature; one of the kind constants:
 * {@link #ARRAY_TYPE_SIGNATURE}, {@link #CLASS_TYPE_SIGNATURE},
 * {@link #BASE_TYPE_SIGNATURE}, or {@link #TYPE_VARIABLE_SIGNATURE},
 * or (since 3.1) {@link #WILDCARD_TYPE_SIGNATURE} or {@link #CAPTURE_TYPE_SIGNATURE}
 * @exception IllegalArgumentException if this is not a type signature
 * @since 3.0
 */
public static int getTypeSignatureKind(String typeSignature) {
	final int[] theType = { CLASS_TYPE_SIGNATURE };
	SignatureProcessor.process(typeSignature, new SignatureRequestorAdapter() {
		private boolean found = false;	
		@Override
		public void acceptSymbol(char type, char[] name, int startPosition, String signature) {
			switch(type) {
			case ISignatureConstants.CLASS: theType[0] = CLASS_TYPE_SIGNATURE; break;
			case ISignatureConstants.INTERFACE: theType[0] = INTERFACE_TYPE_SIGNATURE; break;
			case ISignatureConstants.STRUCT: theType[0] = STRUCT_TYPE_SIGNATURE; break;
			case ISignatureConstants.UNION: theType[0] = UNION_TYPE_SIGNATURE; break;
			case ISignatureConstants.ENUM: theType[0] = ENUM_TYPE_SIGNATURE; break;
			}
		}
		@Override
		public void enterFunctionType() {
			found = true;
		}
	});
	return theType[0];
}

/**
 * Returns the number of parameter types in the given method signature.
 *
 * @param methodSignature the method signature
 * @return the number of parameters
 * @exception IllegalArgumentException if the signature is not syntactically
 *   correct
 */
public static int getParameterCount(String methodSignature) throws IllegalArgumentException {
	final int[] count = { 0 };	
	SignatureProcessor.process(methodSignature, new SignatureRequestorAdapter() {
		private int functionTypeCount = 0;
		private int stack;
		
		public void acceptArgumentModifier(int stc) { 
			if (functionTypeCount == 1) {
				stack++;
			}
		}

		public void enterFunctionType() {
			functionTypeCount++;
		}

		public void exitFunctionType(LINK link, String signature) {
			if (functionTypeCount == 1) {
				// Parameters + Return type = Stack size
				count[0] = stack;
			}
			
			functionTypeCount--;
		}
	});
	return count[0];
}

/**
 * Extracts the parameter type signatures from the given method signature. 
 * The method signature is expected to be dot-based.
 *
 * @param methodSignature the method signature
 * @return the list of parameter type signatures
 * @exception IllegalArgumentException if the signature is syntactically
 *   incorrect
 * 
 * @since 2.0
 */
public static char[][] getParameterTypes(char[] methodSignature) throws IllegalArgumentException {
	return CharOperation.stringArrayToCharArray(getParameterTypes(new String(methodSignature)));
}

/**
 * Extracts the parameter type signatures from the given method signature. 
 * The method signature is expected to be dot-based.
 *
 * @param methodSignature the method signature
 * @return the list of parameter type signatures
 * @exception IllegalArgumentException if the signature is syntactically
 *   incorrect
 */
public static String[] getParameterTypes(String methodSignature) throws IllegalArgumentException {
	ParameterTypesSignatureRequestor requestor = new ParameterTypesSignatureRequestor();
	SignatureProcessor.process(methodSignature, requestor);
	
	if (requestor.stack.isEmpty()) {
		return CharOperation.NO_STRINGS;
	}
	
	Stack<String> stack = requestor.stack;
	if (stack.isEmpty()) {
		return CharOperation.NO_STRINGS;
	}
	
	stack.pop();
	if (stack.isEmpty()) {
		return CharOperation.NO_STRINGS;
	}
	
	String[] parameterTypes = new String[stack.size()];
	int i = parameterTypes.length - 1;
	while(!stack.isEmpty()) {
		parameterTypes[i] = stack.pop();
		i--;
	}
	return parameterTypes;
}

// TODO Descent templates!
private static class ParameterTypesSignatureRequestor extends SignatureRequestorAdapter {
	
	private Stack<String> stack = new Stack<String>();
	private int functionTypeCount;

	public ParameterTypesSignatureRequestor() {
	}
	
	public void acceptArgumentBreak(char c) {
	}

	public void acceptArgumentModifier(int stc) {
		// TODO Descent Signature
	}

	public void acceptAssociativeArray(String signature) {
		if (functionTypeCount != 1) {
			return;
		}
		
		stack.pop();
		stack.pop();
		stack.push(signature);
	}

	public void acceptDelegate(String signature) { 
		if (functionTypeCount != 1) {
			return;
		}
		
		stack.pop();
		stack.push(signature);
	}

	public void acceptDynamicArray(String signature) {
		if (functionTypeCount != 1) {
			return;
		}
		
		stack.pop();
		stack.push(signature);
	}

	public void acceptModule(char[][] compoundName, String signature) {
		if (functionTypeCount != 1) {
			return;
		}
		
		stack.push(signature);
	}

	public void acceptPointer(String signature) {
		if (functionTypeCount != 1) {
			return;
		}
		
		stack.pop();
		stack.push(signature);
	}

	public void acceptPrimitive(TypeBasic type) {
		if (functionTypeCount != 1) {
			return;
		}
		
		stack.push(type.deco);
	}

	public void acceptStaticArray(int dimension, String signature) {
		if (functionTypeCount != 1) {
			return;
		}
		
		stack.pop();
		stack.push(signature);
	}

	public void acceptSymbol(char type, char[] name, int startPosition, String signature) {
		if (functionTypeCount != 1) {
			return;
		}
		
		stack.pop();
		if (type == ISignatureConstants.FUNCTION && !stack.isEmpty()) {
			stack.pop();
		}
		stack.push(signature);
	}

	public void enterFunctionType() {
		functionTypeCount++;
	}

	public void exitFunctionType(LINK link, String signature) {
		if (functionTypeCount != 1) {
			if (!stack.isEmpty()) {
				stack.pop();
			}
			stack.push(signature);
		}
		functionTypeCount--;
	}
	
}

/**
 * Extracts the thrown exception type signatures from the given method signature if any
 * The method signature is expected to be dot-based.
 *
 * @param methodSignature the method signature
 * @return the list of thrown exception type signatures
 * @exception IllegalArgumentException if the signature is syntactically
 *   incorrect
 *
 * @since 3.1
 */
public static String[] getThrownExceptionTypes(String methodSignature) throws IllegalArgumentException {
	char[][] parameterTypes = getThrownExceptionTypes(methodSignature.toCharArray());
	return CharOperation.toStrings(parameterTypes);
}

/**
 * Extracts the thrown exception type signatures from the given method signature if any
 * The method signature is expected to be dot-based.
 *
 * @param methodSignature the method signature
 * @return the list of thrown exception type signatures
 * @exception IllegalArgumentException if the signature is syntactically
 *   incorrect
 *
 * @since 3.1
 */
public static char[][] getThrownExceptionTypes(char[] methodSignature) throws IllegalArgumentException {
	// skip type parameters
	int exceptionStart = CharOperation.indexOf(C_EXCEPTION_START, methodSignature);
	if (exceptionStart == -1) {
		int paren = CharOperation.lastIndexOf(C_PARAM_END, methodSignature);
		if (paren == -1) {
			throw new IllegalArgumentException();
		}
		// ignore return type
		exceptionStart = Util.scanTypeSignature(methodSignature, paren+1) + 1;
		int length = methodSignature.length;
		if (exceptionStart == length) return CharOperation.NO_CHAR_CHAR;
		throw new IllegalArgumentException();
	}
	int length = methodSignature.length;
	int i = exceptionStart;
	ArrayList exceptionList = new ArrayList(1);
	while (i < length) {
		if (methodSignature[i] == C_EXCEPTION_START) {
			exceptionStart++;
			i++;
		} else {
			throw new IllegalArgumentException();			
		}
		i = Util.scanTypeSignature(methodSignature, i) + 1;
		exceptionList.add(CharOperation.subarray(methodSignature, exceptionStart,i));	
		exceptionStart = i;
	}
	char[][] result;
	exceptionList.toArray(result = new char[exceptionList.size()][]);
	return result;
}

/**
 * Extracts the type argument signatures from the given type signature.
 * Returns an empty array if the type signature is not a parameterized type signature.
 *
 * @param parameterizedTypeSignature the parameterized type signature
 * @return the signatures of the type arguments
 * @exception IllegalArgumentException if the signature is syntactically incorrect
 * 
 * @since 3.1
 */
public static char[][] getTypeArguments(char[] parameterizedTypeSignature) throws IllegalArgumentException {
	int length = parameterizedTypeSignature.length;
	if (length < 2 || parameterizedTypeSignature[length-2] != C_GENERIC_END)
		// cannot have type arguments otherwise signature would end by ">;"
		return CharOperation.NO_CHAR_CHAR;
	int count = 1; // start to count generic end/start peers
	int start = length - 2;
	while (start >= 0 && count > 0) {
		switch (parameterizedTypeSignature[--start]) {
			case C_GENERIC_START:
				count--;
				break;
			case C_GENERIC_END:
				count++;
				break;
		}
	}
	if (start < 0) // invalid number of generic start/end
		throw new IllegalArgumentException();
	ArrayList args = new ArrayList();
	int p = start + 1;
	while (true) {
		if (p >= parameterizedTypeSignature.length) {
			throw new IllegalArgumentException();
		}
		char c = parameterizedTypeSignature[p];
		if (c == C_GENERIC_END) {
			int size = args.size();
			char[][] result = new char[size][];
			args.toArray(result);
			return result;
		}
		int e = Util.scanTypeArgumentSignature(parameterizedTypeSignature, p);
		args.add(CharOperation.subarray(parameterizedTypeSignature, p, e+1));
		p = e + 1;
	}
}

/**
 * Extracts the type argument signatures from the given type signature.
 * Returns an empty array if the type signature is not a parameterized type signature.
 *
 * @param parameterizedTypeSignature the parameterized type signature
 * @return the signatures of the type arguments
 * @exception IllegalArgumentException if the signature is syntactically incorrect
 * 
 * @since 3.1
 */
public static String[] getTypeArguments(String parameterizedTypeSignature) throws IllegalArgumentException {
	char[][] args = getTypeArguments(parameterizedTypeSignature.toCharArray());
	return CharOperation.toStrings(args);
}

/**
 * Extracts the type erasure signature from the given parameterized type signature.
 * Returns the given type signature if it is not parameterized.
 * 
 * @param parameterizedTypeSignature the parameterized type signature
 * @return the signature of the type erasure
 * @exception IllegalArgumentException if the signature is syntactically
 *   incorrect
 * 
 * @since 3.1
 */
public static char[] getTypeErasure(char[] parameterizedTypeSignature) throws IllegalArgumentException {
	int end = CharOperation.indexOf(C_GENERIC_START, parameterizedTypeSignature);
	if (end == -1) return parameterizedTypeSignature;
	int length = parameterizedTypeSignature.length;
	char[] result = new char[length];
	int pos = 0;
	int start = 0;
	int deep= 0;
	for (int idx=end; idx<length; idx++) {
		switch (parameterizedTypeSignature[idx]) {
			case C_GENERIC_START:
				if (deep == 0) {
					int size = idx-start;
					System.arraycopy(parameterizedTypeSignature, start, result, pos, size);
					end = idx;
					pos += size;
				}
				deep++;
				break;
			case C_GENERIC_END:
				deep--;
				if (deep < 0) throw new IllegalArgumentException();
				if (deep == 0) start = idx+1;
				break;
		}
	}
	if (deep > 0) throw new IllegalArgumentException();
	int size = pos+length-start;
	char[] resized = new char[size];
	System.arraycopy(result, 0, resized, 0, pos);
	System.arraycopy(parameterizedTypeSignature, start, resized, pos, length-start);
	return resized;
}

/**
 * Extracts the type erasure signature from the given parameterized type signature.
 * Returns the given type signature if it is not parameterized.
 * 
 * @param parameterizedTypeSignature the parameterized type signature
 * @return the signature of the type erasure
 * @exception IllegalArgumentException if the signature is syntactically
 *   incorrect
 * 
 * @since 3.1
 */
public static String getTypeErasure(String parameterizedTypeSignature) throws IllegalArgumentException {
	return new String(getTypeErasure(parameterizedTypeSignature.toCharArray()));
}

/**
 * Extracts the type parameter signatures from the given method or type signature. 
 * The method or type signature is expected to be dot-based.
 *
 * @param methodOrTypeSignature the method or type signature
 * @return the list of type parameter signatures
 * @exception IllegalArgumentException if the signature is syntactically
 *   incorrect
 * 
 * @since 3.1
 */
public static char[][] getTypeParameters(char[] methodOrTypeSignature) throws IllegalArgumentException {
	try {
		int length = methodOrTypeSignature.length;
		if (length == 0) return CharOperation.NO_CHAR_CHAR;
		if (methodOrTypeSignature[0] != C_GENERIC_START) return CharOperation.NO_CHAR_CHAR;
		
		ArrayList paramList = new ArrayList(1);
		int paramStart = 1, i = 1;  // start after leading '<'
		while (i < length) {
			if (methodOrTypeSignature[i] == C_GENERIC_END) {
				int size = paramList.size();
				if (size == 0) throw new IllegalArgumentException(); 
				char[][] result;
				paramList.toArray(result = new char[size][]);
				return result;
			}
			i = CharOperation.indexOf(C_COLON, methodOrTypeSignature, i);
			if (i < 0 || i >= length) 
				throw new IllegalArgumentException();
			// iterate over bounds
			while (methodOrTypeSignature[i] == ':') {
				i++; // skip colon
				switch (methodOrTypeSignature[i]) {
					case ':':
						// no class bound
						break; 
					case C_GENERIC_END:
						break;
					case C_RESOLVED:
						try {
							i = Util.scanClassTypeSignature(methodOrTypeSignature, i);
							i++; // position at start of next param if any
						} catch (IllegalArgumentException e) {
							// not a class type signature -> it is a new type parameter
						}
						break;
					case C_ARRAY:
						try {
							i = Util.scanArrayTypeSignature(methodOrTypeSignature, i);
							i++; // position at start of next param if any
						} catch (IllegalArgumentException e) {
							// not an array type signature -> it is a new type parameter
						}
						break;
					case C_TYPE_VARIABLE:
						try {
							i = Util.scanTypeVariableSignature(methodOrTypeSignature, i);
							i++; // position at start of next param if any
						} catch (IllegalArgumentException e) {
							// not a type variable signature -> it is a new type parameter
						}							
						break;
					// default: another type parameter is starting
				}
			}
			paramList.add(CharOperation.subarray(methodOrTypeSignature, paramStart, i));
			paramStart = i; // next param start from here
		}
	} catch (ArrayIndexOutOfBoundsException e) {
		// invalid signature, fall through
	}
	throw new IllegalArgumentException();
}
/**
 * Extracts the type parameter signatures from the given method or type signature. 
 * The method or type signature is expected to be dot-based.
 *
 * @param methodOrTypeSignature the method or type signature
 * @return the list of type parameter signatures
 * @exception IllegalArgumentException if the signature is syntactically
 *   incorrect
 * 
 * @since 3.1
 */
public static String[] getTypeParameters(String methodOrTypeSignature) throws IllegalArgumentException {
	char[][] params = getTypeParameters(methodOrTypeSignature.toCharArray());
	return CharOperation.toStrings(params);
}

/**
 * Extracts the type variable name from the given formal type parameter
 * signature. The signature is expected to be dot-based.
 *
 * @param formalTypeParameterSignature the formal type parameter signature
 * @return the name of the type variable
 * @exception IllegalArgumentException if the signature is syntactically
 *   incorrect
 * @since 3.0
 */
public static String getTypeVariable(String formalTypeParameterSignature) throws IllegalArgumentException {
	return new String(getTypeVariable(formalTypeParameterSignature.toCharArray()));
}

/**
 * Extracts the type variable name from the given formal type parameter
 * signature. The signature is expected to be dot-based.
 *
 * @param formalTypeParameterSignature the formal type parameter signature
 * @return the name of the type variable
 * @exception IllegalArgumentException if the signature is syntactically
 *   incorrect
 * @since 3.0
 */
public static char[] getTypeVariable(char[] formalTypeParameterSignature) throws IllegalArgumentException {
	int p = CharOperation.indexOf(C_COLON, formalTypeParameterSignature);
	if (p < 0) {
		// no ":" means can't be a formal type parameter signature
		throw new IllegalArgumentException();
	}
	return CharOperation.subarray(formalTypeParameterSignature, 0, p);
}

/**
 * Extracts the class and interface bounds from the given formal type
 * parameter signature. The class bound, if present, is listed before
 * the interface bounds. The signature is expected to be dot-based.
 *
 * @param formalTypeParameterSignature the formal type parameter signature
 * @return the (possibly empty) list of type signatures for the bounds
 * @exception IllegalArgumentException if the signature is syntactically
 *   incorrect
 * @since 3.0
 */
public static char[][] getTypeParameterBounds(char[] formalTypeParameterSignature) throws IllegalArgumentException {
	int p1 = CharOperation.indexOf(C_COLON, formalTypeParameterSignature);
	if (p1 < 0) {
		// no ":" means can't be a formal type parameter signature
		throw new IllegalArgumentException();
	}
	if (p1 == formalTypeParameterSignature.length - 1) {
		// no class or interface bounds
		return CharOperation.NO_CHAR_CHAR;
	}
	int p2 = CharOperation.indexOf(C_COLON, formalTypeParameterSignature, p1 + 1);
	char[] classBound;
	if (p2 < 0) {
		// no interface bounds
		classBound = CharOperation.subarray(formalTypeParameterSignature, p1 + 1, formalTypeParameterSignature.length);
		return new char[][] {classBound};
	}
	if (p2 == p1 + 1) {
		// no class bound, but 1 or more interface bounds
		classBound = null;
	} else {
		classBound = CharOperation.subarray(formalTypeParameterSignature, p1 + 1, p2);
	}
	char[][] interfaceBounds = CharOperation.splitOn(C_COLON, formalTypeParameterSignature, p2 + 1, formalTypeParameterSignature.length);
	if (classBound == null) {
		return interfaceBounds;
	}
	int resultLength = interfaceBounds.length + 1;
	char[][] result = new char[resultLength][];
	result[0] = classBound;
	System.arraycopy(interfaceBounds, 0, result, 1, interfaceBounds.length);
	return result;
}

/**
 * Extracts the class and interface bounds from the given formal type
 * parameter signature. The class bound, if present, is listed before
 * the interface bounds. The signature is expected to be dot-based.
 *
 * @param formalTypeParameterSignature the formal type parameter signature
 * @return the (possibly empty) list of type signatures for the bounds
 * @exception IllegalArgumentException if the signature is syntactically
 *   incorrect
 * @since 3.0
 */
public static String[] getTypeParameterBounds(String formalTypeParameterSignature) throws IllegalArgumentException {
	char[][] bounds = getTypeParameterBounds(formalTypeParameterSignature.toCharArray());
	return CharOperation.toStrings(bounds);
}

/**
 * Returns a char array containing all but the last segment of the given 
 * dot-separated qualified name. Returns the empty char array if it is not qualified.
 * <p>
 * For example:
 * <pre>
 * <code>
 * getQualifier({'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g', '.', 'O', 'b', 'j', 'e', 'c', 't'}) -> {'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g'}
 * getQualifier({'O', 'u', 't', 'e', 'r', '.', 'I', 'n', 'n', 'e', 'r'}) -> {'O', 'u', 't', 'e', 'r'}
 * getQualifier({'j', 'a', 'v', 'a', '.', 'u', 't', 'i', 'l', '.', 'L', 'i', 's', 't', '<', 'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g', '.', 'S', 't', 'r', 'i', 'n', 'g', '>'}) -> {'j', 'a', 'v', 'a', '.', 'u', 't', 'i', 'l'}
 * </code>
 * </pre>
 * </p>
 *
 * @param name the name
 * @return the qualifier prefix, or the empty char array if the name contains no
 *   dots
 * @exception NullPointerException if name is null
 * @since 2.0
 */
public static char[] getQualifier(char[] name) {
	int firstGenericStart = CharOperation.indexOf(C_GENERIC_START, name);
	int lastDot = CharOperation.lastIndexOf(C_DOT, name, 0, firstGenericStart == -1 ? name.length-1 : firstGenericStart);
	if (lastDot == -1) {
		return CharOperation.NO_CHAR;
	}
	return CharOperation.subarray(name, 0, lastDot);
}
/**
 * Returns a string containing all but the last segment of the given 
 * dot-separated qualified name. Returns the empty string if it is not qualified.
 * <p>
 * For example:
 * <pre>
 * <code>
 * getQualifier("java.lang.Object") -&gt; "java.lang"
 * getQualifier("Outer.Inner") -&gt; "Outer"
 * getQualifier("java.util.List&lt;java.lang.String&gt;") -&gt; "java.util"
 * </code>
 * </pre>
 * </p>
 *
 * @param name the name
 * @return the qualifier prefix, or the empty string if the name contains no
 *   dots
 * @exception NullPointerException if name is null
 */
public static String getQualifier(String name) {
	char[] qualifier = getQualifier(name.toCharArray());
	if (qualifier.length == 0) return EMPTY;
	return new String(qualifier);
}
/**
 * Extracts the return type from the given method signature. The method signature is 
 * expected to be dot-based.
 *
 * @param methodSignature the method signature
 * @return the type signature of the return type
 * @exception IllegalArgumentException if the signature is syntactically
 *   incorrect
 * 
 * @since 2.0
 */
public static char[] getReturnType(char[] methodSignature) throws IllegalArgumentException {
	return getReturnType(new String(methodSignature)).toCharArray();
}
/**
 * Extracts the return type from the given method signature. The method signature is 
 * expected to be dot-based.
 *
 * @param methodSignature the method signature
 * @return the type signature of the return type
 * @exception IllegalArgumentException if the signature is syntactically
 *   incorrect
 */
public static String getReturnType(String methodSignature) throws IllegalArgumentException {
	ParameterTypesSignatureRequestor requestor = new ParameterTypesSignatureRequestor();
	SignatureProcessor.process(methodSignature, requestor);
	
	if (requestor.stack.isEmpty()) {
		return CharOperation.NO_STRINGS[0];
	}
	
	return requestor.stack.pop();
}
/**
 * Returns package fragment of a type signature. The package fragment separator must be '.'
 * and the type fragment separator must be '$'.
 * <p>
 * For example:
 * <pre>
 * <code>
 * getSignatureQualifier({'L', 'j', 'a', 'v', 'a', '.', 'u', 't', 'i', 'l', '.', 'M', 'a', 'p', '$', 'E', 'n', 't', 'r', 'y', ';'}) -> {'j', 'a', 'v', 'a', '.', 'u', 't', 'i', 'l'}
 * </code>
 * </pre>
 * </p>
 * 
 * @param typeSignature the type signature
 * @return the package fragment (separators are '.')
 * @since 3.1
 */
public static char[] getSignatureQualifier(char[] typeSignature) {
	if(typeSignature == null) return CharOperation.NO_CHAR;
	
	char[] qualifiedType = Signature.toCharArray(typeSignature);
	
	int dotCount = 0;
	indexFound: for(int i = 0; i < typeSignature.length; i++) {
		switch(typeSignature[i]) {
			case C_DOT:
				dotCount++;
				break;
			case C_GENERIC_START:
				break indexFound;
			case C_DOLLAR:
				break indexFound;
		}
	}
	
	if(dotCount > 0) {
		for(int i = 0; i < qualifiedType.length; i++) {
			if(qualifiedType[i] == '.') {
				dotCount--;
			}
			if(dotCount <= 0) {
				return CharOperation.subarray(qualifiedType, 0, i);
			}
		}
	}
	return CharOperation.NO_CHAR;
}
/**
 * Returns package fragment of a type signature. The package fragment separator must be '.'
 * and the type fragment separator must be '$'.
 * <p>
 * For example:
 * <pre>
 * <code>
 * getSignatureQualifier("Ljava.util.Map$Entry") -> "java.util"
 * </code>
 * </pre>
 * </p>
 * 
 * @param typeSignature the type signature
 * @return the package fragment (separators are '.')
 * @since 3.1
 */
public static String getSignatureQualifier(String typeSignature) {
	return new String(getSignatureQualifier(typeSignature == null ? null : typeSignature.toCharArray()));
}
/**
 * Returns type fragment of a type signature. The package fragment separator must be '.'
 * and the type fragment separator must be '$'.
 * <p>
 * For example:
 * <pre>
 * <code>
 * getSignatureSimpleName({'L', 'j', 'a', 'v', 'a', '.', 'u', 't', 'i', 'l', '.', 'M', 'a', 'p', '$', 'E', 'n', 't', 'r', 'y', ';'}) -> {'M', 'a', 'p', '.', 'E', 'n', 't', 'r', 'y'}
 * </code>
 * </pre>
 * </p>
 * 
 * @param typeSignature the type signature
 * @return the type fragment (separators are '.')
 * @since 3.1
 */
public static char[] getSignatureSimpleName(char[] typeSignature) {
	if(typeSignature == null) return CharOperation.NO_CHAR;
	
	char[] qualifiedType = Signature.toCharArray(typeSignature);
	
	int dotCount = 0;
	indexFound: for(int i = 0; i < typeSignature.length; i++) {
		switch(typeSignature[i]) {
			case C_DOT:
				dotCount++;
				break;
			case C_GENERIC_START:
				break indexFound;
			case C_DOLLAR:
				break indexFound;
		}
	}
	
	if(dotCount > 0) {
		for(int i = 0; i < qualifiedType.length; i++) {
			if(qualifiedType[i] == '.') {
				dotCount--;
			}
			if(dotCount <= 0) {
				return CharOperation.subarray(qualifiedType, i + 1, qualifiedType.length);
			}
		}
	}
	return qualifiedType;
}
/**
 * Returns type fragment of a type signature. The package fragment separator must be '.'
 * and the type fragment separator must be '$'.
 * <p>
 * For example:
 * <pre>
 * <code>
 * getSignatureSimpleName("Ljava.util.Map$Entry") -> "Map.Entry"
 * </code>
 * </pre>
 * </p>
 * 
 * @param typeSignature the type signature
 * @return the type fragment (separators are '.')
 * @since 3.1
 */
public static String getSignatureSimpleName(String typeSignature) {
	return new String(getSignatureSimpleName(typeSignature == null ? null : typeSignature.toCharArray()));
}
	
/**
 * Returns the last segment of the given dot-separated qualified name.
 * Returns the given name if it is not qualified.
 * <p>
 * For example:
 * <pre>
 * <code>
 * getSimpleName({'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g', '.', 'O', 'b', 'j', 'e', 'c', 't'}) -> {'O', 'b', 'j', 'e', 'c', 't'}
 * </code>
 * </pre>
 * </p>
 *
 * @param name the name
 * @return the last segment of the qualified name
 * @exception NullPointerException if name is null
 * @since 2.0
 */
public static char[] getSimpleName(char[] name) {
	int lastDot = -1, lastGenericStart = -1, lastGenericEnd = -1;
	int depth = 0;
	int length = name.length;
	lastDotLookup: for (int i = length -1; i >= 0; i--) {
		switch (name[i]) {
			case '.':
				if (depth == 0) {
					lastDot = i;
					break lastDotLookup;
				}
				break;
			case '<':
				depth--;
				if (depth == 0) lastGenericStart = i;
				break;
			case '>':
				if (depth == 0) lastGenericEnd = i;
				depth++;
				break;
		}
	}
	if (lastGenericStart < 0) {
		if (lastDot < 0) {
			return name;
		}
		return  CharOperation.subarray(name, lastDot + 1, length);
	}
	StringBuffer buffer = new StringBuffer(10);
	int nameStart = lastDot < 0 ? 0 : lastDot+1;
	buffer.append(name, nameStart, lastGenericStart - nameStart);
	appendArgumentSimpleNames(name, lastGenericStart, lastGenericEnd, buffer);
	buffer.append(name, lastGenericEnd+1, length-lastGenericEnd-1); // copy trailing portion, may contain dimensions	
	char[] result = new char[length = buffer.length()];
	buffer.getChars(0, length, result, 0);
	return result;
}
/**
 * Returns the last segment of the given dot-separated qualified name.
 * Returns the given name if it is not qualified.
 * <p>
 * For example:
 * <pre>
 * <code>
 * getSimpleName("java.lang.Object") -&gt; "Object"
 * </code>
 * <code>
 * getSimpleName("java.util.Map&lt;java.lang.String, java.lang.Object&gt;") -&gt; "Map&lt;String,Object&gt;"
 * </code>
 * </pre>
 * </p>
 *
 * @param name the name
 * @return the last segment of the qualified name
 * @exception NullPointerException if name is null
 */
public static String getSimpleName(String name) {
	int lastDot = -1, lastGenericStart = -1, lastGenericEnd = -1;
	int depth = 0;
	int length = name.length();
	lastDotLookup: for (int i = length -1; i >= 0; i--) {
		switch (name.charAt(i)) {
			case '.':
				if (depth == 0) {
					lastDot = i;
					break lastDotLookup;
				}
				break;
			case '<':
				depth--;
				if (depth == 0) lastGenericStart = i;
				break;
			case '>':
				if (depth == 0) lastGenericEnd = i;
				depth++;
				break;
		}
	}
	if (lastGenericStart < 0) {
		if (lastDot < 0) {
			return name;
		}
		return name.substring(lastDot + 1, length);
	}
	StringBuffer buffer = new StringBuffer(10);
	char[] nameChars = name.toCharArray();
	int nameStart = lastDot < 0 ? 0 : lastDot+1;
	buffer.append(nameChars, nameStart, lastGenericStart - nameStart);
	appendArgumentSimpleNames(nameChars, lastGenericStart, lastGenericEnd, buffer);
	buffer.append(nameChars, lastGenericEnd+1, length-lastGenericEnd-1); // copy trailing portion, may contain dimensions	
	return buffer.toString();
}

private static void appendSimpleName(char[] name, int start, int end, StringBuffer buffer) {
	int lastDot = -1, lastGenericStart = -1, lastGenericEnd = -1;
	int depth = 0;
	if (name[start] == '?') { // wildcard
		buffer.append("?"); //$NON-NLS-1$
		int index = consumeWhitespace(name, start+1, end+1);
		switch (name[index]) {
			case 'e' :
				int checkPos = checkName(EXTENDS, name, index, end);
			    if (checkPos > 0) {
			        buffer.append(' ').append(EXTENDS).append(' ');
			        index = consumeWhitespace(name, checkPos, end+1);
				}
				break;
			case 's' :
				checkPos = checkName(SUPER, name, index, end+1);
			    if (checkPos > 0) {
			        buffer.append(' ').append(SUPER).append(' ');
			        index = consumeWhitespace(name, checkPos, end+1);
				}
				break;
		}
		start = index; // leading segment got processed
	}
	lastDotLookup: for (int i = end; i >= start; i--) {
		switch (name[i]) {
			case '.':
				if (depth == 0) {
					lastDot = i;
					if (name[start] == C_EXTENDS || name[start] == C_SUPER) {
						buffer.append(name[start]);
					}
					break lastDotLookup;
				}
				break;
			case '<':
				depth--;
				if (depth == 0) lastGenericStart = i;
				break;
			case '>':
				if (depth == 0) lastGenericEnd = i;
				depth++;
				break;
		}
	}
	int nameStart = lastDot < 0 ? start : lastDot+1;
	int nameEnd = lastGenericStart < 0 ? end+1 : lastGenericStart;
	buffer.append(name, nameStart, nameEnd - nameStart);
	if (lastGenericStart >= 0) {
		appendArgumentSimpleNames(name, lastGenericStart, lastGenericEnd, buffer);
		buffer.append(name, lastGenericEnd+1, end - lastGenericEnd); // copy trailing portion, may contain dimensions
	}
}
// <x.y.z, a.b<c>.d<e.f>> --> <z,d<f>>
private static void appendArgumentSimpleNames(char[] name, int start, int end, StringBuffer buffer) {
	buffer.append('<');
	int depth = 0;
	int argumentStart = -1;
	int argumentCount = 0;
	for (int i = start; i <= end; i++) {
		switch(name[i]) {
			case '<' :
				depth++;
				if (depth == 1) {
					argumentStart = i+1;
				}
				break;
			case '>' : 
				if (depth == 1) {
					if (argumentCount > 0) buffer.append(',');
					appendSimpleName(name, argumentStart, i-1, buffer);
					argumentCount++;
				}
				depth--;
				break;
			case ',' :
				if (depth == 1) {
					if (argumentCount > 0) buffer.append(',');
					appendSimpleName(name, argumentStart, i-1, buffer);
					argumentCount++;
					argumentStart = i+1;					
				}
				break;
		}
	}
	buffer.append('>');
}
/**
 * Returns all segments of the given dot-separated qualified name.
 * Returns an array with only the given name if it is not qualified.
 * Returns an empty array if the name is empty.
 * <p>
 * For example:
 * <pre>
 * <code>
 * getSimpleNames({'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g', '.', 'O', 'b', 'j', 'e', 'c', 't'}) -> {{'j', 'a', 'v', 'a'}, {'l', 'a', 'n', 'g'}, {'O', 'b', 'j', 'e', 'c', 't'}}
 * getSimpleNames({'O', 'b', 'j', 'e', 'c', 't'}) -> {{'O', 'b', 'j', 'e', 'c', 't'}}
 * getSimpleNames({}) -> {}
 * getSimpleNames({'j', 'a', 'v', 'a', '.', 'u', 't', 'i', 'l', '.', 'L', 'i', 's', 't', '<', 'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g', '.', 'S', 't', 'r', 'i', 'n', 'g', '>'}) -> {{'j', 'a', 'v', 'a'}, {'l', 'a', 'n', 'g'}, {'L', 'i', 's', 't', '<', 'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g', '.', 'S', 't', 'r', 'i', 'n', 'g'}}
 * </code>
 * </pre>
 *
 * @param name the name
 * @return the list of simple names, possibly empty
 * @exception NullPointerException if name is null
 * @since 2.0
 */
public static char[][] getSimpleNames(char[] name) {
	int length = name == null ? 0 : name.length;
	if (length == 0)
		return CharOperation.NO_CHAR_CHAR;
	
	int wordCount = 1;
	countingWords: for (int i = 0; i < length; i++)
		switch(name[i]) {
			case C_DOT:
				wordCount++;
				break;
			case C_GENERIC_START:
				break countingWords;
		}
	char[][] split = new char[wordCount][];
	int last = 0, currentWord = 0;
	for (int i = 0; i < length; i++) {
		if (name[i] == C_GENERIC_START) break;
		if (name[i] == C_DOT) {
			split[currentWord] = new char[i - last];
			System.arraycopy(
				name,
				last,
				split[currentWord++],
				0,
				i - last);
			last = i + 1;
		}
	}
	split[currentWord] = new char[length - last];
	System.arraycopy(name, last, split[currentWord], 0, length - last);
	return split;
}
/**
 * Returns all segments of the given dot-separated qualified name.
 * Returns an array with only the given name if it is not qualified.
 * Returns an empty array if the name is empty.
 * <p>
 * For example:
 * <pre>
 * <code>
 * getSimpleNames("java.lang.Object") -&gt; {"java", "lang", "Object"}
 * getSimpleNames("Object") -&gt; {"Object"}
 * getSimpleNames("") -&gt; {}
 * getSimpleNames("java.util.List&lt;java.lang.String&gt;") -&gt; 
 *   {"java", "util", "List&lt;java.lang.String&gt;"}
 * </code>
 * </pre>
 *
 * @param name the name
 * @return the list of simple names, possibly empty
 * @exception NullPointerException if name is null
 */
public static String[] getSimpleNames(String name) {
	return CharOperation.toStrings(getSimpleNames(name.toCharArray()));
}

/**
 * Removes any capture information from the given type or method signature
 * and returns the resulting signature.
 * Returns the type or method signature itself if no capture information is
 * present.
 * <p>
 * For example (using equivalent string-based method):
 * <pre>
 * <code>
 * removeCapture("LTest&lt;!+Ljava.lang.Throwable;&gt;;")
 * will return: "LTest&lt;+Ljava.lang.Throwable;&gt;;"
 * </code>
 * </pre>
 * </p>
 *
 * @param methodOrTypeSignature the signature which may have been captured
 * @return a new signature without capture information or the signature itself
 * 	if no specific capture information is present
 * @exception NullPointerException if <code>methodOrTypeSignature</code> is null
 *
 * @since 3.1
 */
public static char[] removeCapture(char[] methodOrTypeSignature) {
	return CharOperation.remove(methodOrTypeSignature, C_CAPTURE);
}
	
/**
 * Removes any capture information from the given type or method signature
 * and returns the resulting signature.
 * Returns the type or method signature itself if no capture information is
 * present.
 * <p>
 * For example:
 * <pre>
 * <code>
 * removeCapture("LTest&lt;!+Ljava.lang.Throwable;&gt;;")
 * will return: "LTest&lt;+Ljava.lang.Throwable;&gt;;"
 * </code>
 * </pre>
 * </p>
 *
 * @param methodOrTypeSignature the signature which may have been captured
 * @return a new signature without capture information or the signature itself
 * 	if no specific capture information is present
 * @exception NullPointerException if <code>methodOrTypeSignature</code> is null
 *
 * @since 3.1
 */
public static String removeCapture(String methodOrTypeSignature) {
	char[] array = methodOrTypeSignature.toCharArray();
	char[] result = removeCapture(array);
	if (array == result) return methodOrTypeSignature;
	return new String(result);
}

/**
 * Converts the given method signature to a readable form. The method signature is expected to
 * be dot-based.
 * <p>
 * For example:
 * <pre>
 * <code>
 * toString("([Ljava.lang.String;)V", "main", new String[] {"args"}, false, true) -> "void main(String[] args)"
 * </code>
 * </pre>
 * </p>
 * 
 * @param methodSignature the method signature to convert
 * @param methodName the name of the method to insert in the result, or 
 *   <code>null</code> if no method name is to be included
 * @param parameterNames the parameter names to insert in the result, or 
 *   <code>null</code> if no parameter names are to be included; if supplied,
 *   the number of parameter names must match that of the method signature
 * @param fullyQualifyTypeNames <code>true</code> if type names should be fully
 *   qualified, and <code>false</code> to use only simple names
 * @param includeReturnType <code>true</code> if the return type is to be
 *   included
 * @return the char array representation of the method signature
 * 
 * @since 2.0
 */
public static char[] toCharArray(char[] methodSignature, char[] methodName, char[][] parameterNames, boolean fullyQualifyTypeNames, boolean includeReturnType) {
	return toCharArray(methodSignature, methodName, parameterNames, fullyQualifyTypeNames, includeReturnType, false);
}
/**
 * Converts the given method signature to a readable form. The method signature is expected to
 * be dot-based.
 * <p>
 * For example:
 * <pre>
 * <code>
 * toString("([Ljava.lang.String;)V", "main", new String[] {"args"}, false, true) -> "void main(String[] args)"
 * </code>
 * </pre>
 * </p>
 * 
 * @param methodSignature the method signature to convert
 * @param methodName the name of the method to insert in the result, or 
 *   <code>null</code> if no method name is to be included
 * @param parameterNames the parameter names to insert in the result, or 
 *   <code>null</code> if no parameter names are to be included; if supplied,
 *   the number of parameter names must match that of the method signature
 * @param fullyQualifyTypeNames <code>true</code> if type names should be fully
 *   qualified, and <code>false</code> to use only simple names
 * @param includeReturnType <code>true</code> if the return type is to be
 *   included
 * @param isVargArgs <code>true</code> if the last argument should be displayed as a 
 * variable argument,  <code>false</code> otherwise.
 * @return the char array representation of the method signature
 * 
 * @since 3.1
 */
public static char[] toCharArray(char[] methodSignature, char[] methodName, char[][] parameterNames, boolean fullyQualifyTypeNames, boolean includeReturnType, boolean isVargArgs) {
	ToCharArraySignatureRequestor requestor = new ToCharArraySignatureRequestor(fullyQualifyTypeNames);
	
	SignatureProcessor.process(new String(methodSignature), requestor);
	
	if (!requestor.stack.isEmpty()) {
		Stack<String> stack = requestor.stack.pop();
		if (!stack.isEmpty()) {
			StringBuilder sb = new StringBuilder();		
			appendFunction(stack, sb, methodName, parameterNames, includeReturnType);
			char[] array = new char[sb.length()];
			sb.getChars(0, sb.length(), array, 0);
			return array;
		}
	}
	
	return CharOperation.NO_CHAR;
}

private final static char[] FUNCTION = "function".toCharArray();

// TODO Descent templates!
private static class ToCharArraySignatureRequestor extends SignatureRequestorAdapter {
	
	private Stack<Stack<String>> stack = new Stack<Stack<String>>();
	private int functionTypeCount;
	private boolean fullyQualifyTypeNames;
	
	private boolean forFunction = true;

	public ToCharArraySignatureRequestor(boolean fullyQualifyTypeNames) {
		this.fullyQualifyTypeNames = fullyQualifyTypeNames;
	}
	
	public void acceptArgumentBreak(char c) {
	}

	public void acceptArgumentModifier(int stc) {
		// TODO Descent Signature
	}
	
	@Override
	public void acceptIdentifier(char[] name, String signature) {
		Stack<String> stack = new Stack<String>();
		stack.push(new String(name));
		
		this.stack.push(stack);		
	}

	public void acceptAssociativeArray(String signature) {
		Stack<String> stack = this.stack.peek();
		String value = stack.pop();
		String key = stack.pop();
		stack.push(value + "[" + key + "]");
	}

	public void acceptDelegate(String signature) { 
		Stack<String> stack = this.stack.peek();
		String value = stack.peek();
		int index = value.indexOf("function");
		if (index != -1) {
			stack.pop();
			stack.push(value.substring(0, index) + "delegate" + value.substring(index + 8)); 
		}
	}

	public void acceptDynamicArray(String signature) {
		Stack<String> stack = this.stack.peek();
		stack.push(stack.pop() + "[]");
	}

	public void acceptModule(char[][] compoundName, String signature) {
		Stack<String> stack;
		if (forFunction) {
			if (this.stack.isEmpty()) {
				return;
			}
			
			stack = this.stack.peek();
		} else {
			stack = new Stack<String>();
			this.stack.push(stack);
		}
		stack.push(CharOperation.toString(compoundName));
	}

	public void acceptPointer(String signature) {
		Stack<String> stack = this.stack.peek();
		stack.push(stack.pop() + "*");
	}

	public void acceptPrimitive(TypeBasic type) {
		Stack<String> stack;
		if (this.stack.isEmpty()) {
			stack = new Stack<String>();
			this.stack.push(stack);
		} else {
			stack = this.stack.peek();
		}
		stack.push(type.ty.name);
	}

	public void acceptStaticArray(int dimension, String signature) {
		Stack<String> stack = this.stack.peek();
		stack.push(stack.pop() + "[" + dimension + "]");
	}

	public void acceptSymbol(char type, char[] name, int startPosition, String signature) {
		if (forFunction) {
			if (this.stack.isEmpty()) {
				return;
			}
		}
		
		Stack<String> stack = this.stack.peek();
		String prev = stack.pop();
		if (type == ISignatureConstants.FUNCTION && !stack.isEmpty()) {
			String one = stack.pop();
			if (fullyQualifyTypeNames) {
				stack.push(one + "." + String.valueOf(name) + stripFunctionOrDelegate(prev));
			} else {
				stack.push(String.valueOf(name));
			}
		} else {
			if (fullyQualifyTypeNames) {
				stack.push(prev + "." + String.valueOf(name));
			} else {
				stack.push(String.valueOf(name));
			}
		}
	}
	
	private String stripFunctionOrDelegate(String value) {
		int parenCount = 0;
		int bracketCount = 0;
		for(int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			switch(c) {
			case '(':
				parenCount++;
				break;
			case ')':
				parenCount--;
				break;
			case '[':
				bracketCount++;
				break;
			case ']':
				bracketCount--;
				break;
			case ' ':
				if (parenCount == 0 && bracketCount == 0) {
					int index = value.indexOf('(', i);
					if (index == -1) {
						return value;
					} else {
						return value.substring(index);
					}
				}
				break;
			}
		}
		
		return value;
	}

	public void enterFunctionType() {
		functionTypeCount++;
		this.stack.push(new Stack<String>());
	}

	public void exitFunctionType(LINK link, String signature) {
		if (stack.size() > 1) {
			Stack<String> stack = this.stack.pop();
			
			StringBuilder sb = new StringBuilder();
			appendFunction(stack, sb, FUNCTION, null, true);
			
			this.stack.peek().push(sb.toString());
		}
		functionTypeCount--;
	}
	
}

private static <T> Stack<T> reverse(Stack<T> s) {
	Stack<T> other = new Stack<T>();
	while(!s.isEmpty()) {
		other.push(s.pop());
	}
	return other;
}

private static void appendFunction(Stack<String> stack, StringBuilder sb, char[] methodName, char[][] parameterNames, boolean includeReturnType) {
	if (includeReturnType) {
		sb.append(stack.pop());
		sb.append(' ');
	} else {
		stack.pop();
	}
	if (methodName != null) {
		sb.append(methodName);
	}
	sb.append('(');
	
	// Invert parameter order
	stack = reverse(stack);
	
	int i = 0;
	while(!stack.isEmpty()) {
		if (i != 0) {
			sb.append(", ");
		}
		
		sb.append(stack.pop());
		if (parameterNames != null) {
			sb.append(' ');
			sb.append(parameterNames[i]);
		}
		
		i++;
	}
	
	sb.append(')');
}

/**
 * Converts the given type signature to a readable string. The signature is expected to
 * be dot-based.
 * 
 * <p>
 * For example:
 * <pre>
 * <code>
 * toString({'[', 'L', 'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g', '.', 'S', 't', 'r', 'i', 'n', 'g', ';'}) -> {'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g', '.', 'S', 't', 'r', 'i', 'n', 'g', '[', ']'}
 * toString({'I'}) -> {'i', 'n', 't'}
 * toString({'+', 'L', 'O', 'b', 'j', 'e', 'c', 't', ';'}) -> {'?', ' ', 'e', 'x', 't', 'e', 'n', 'd', 's', ' ', 'O', 'b', 'j', 'e', 'c', 't'}
 * </code>
 * </pre>
 * </p>
 * <p>
 * Note: This method assumes that a type signature containing a <code>'$'</code>
 * is an inner type signature. While this is correct in most cases, someone could 
 * define a non-inner type name containing a <code>'$'</code>. Handling this 
 * correctly in all cases would have required resolving the signature, which 
 * generally not feasible.
 * </p>
 *
 * @param signature the type signature
 * @return the string representation of the type
 * @exception IllegalArgumentException if the signature is not syntactically
 *   correct
 * 
 * @since 2.0
 */
public static char[] toCharArray(char[] signature) throws IllegalArgumentException {
	return toString(new String(signature)).toCharArray();
}

/**
 * Scans the given string for a type signature starting at the given
 * index and appends it to the given buffer, and returns the index of the last
 * character.
 * 
 * @param string the signature string
 * @param start the 0-based character index of the first character
 * @param fullyQualifyTypeNames <code>true</code> if type names should be fully
 *   qualified, and <code>false</code> to use only simple names
 * @param buffer the string buffer to append to
 * @return the 0-based character index of the last character
 * @exception IllegalArgumentException if this is not a type signature
 * @see Util#scanTypeSignature(char[], int)
 */
private static int appendTypeSignature(char[] string, int start, boolean fullyQualifyTypeNames, StringBuffer buffer) {
	return appendTypeSignature(string, start, fullyQualifyTypeNames, buffer, false);
}
/**
 * Scans the given string for a type signature starting at the given
 * index and appends it to the given buffer, and returns the index of the last
 * character.
 * 
 * @param string the signature string
 * @param start the 0-based character index of the first character
 * @param fullyQualifyTypeNames <code>true</code> if type names should be fully
 *   qualified, and <code>false</code> to use only simple names
 * @param buffer the string buffer to append to
 * @param isVarArgs <code>true</code> if the type must be displayed as a
 * variable argument, <code>false</code> otherwise. In this case, the type must be an array type
 * @return the 0-based character index of the last character
 * @exception IllegalArgumentException if this is not a type signature, or if isVarArgs is <code>true</code>,
 * and the type is not an array type signature.
 * @see Util#scanTypeSignature(char[], int)
 */
private static int appendTypeSignature(char[] string, int start, boolean fullyQualifyTypeNames, StringBuffer buffer, boolean isVarArgs) {
	// need a minimum 1 char
	if (start >= string.length) {
		throw new IllegalArgumentException();
	}
	char c = string[start];
	if (isVarArgs) {
		switch (c) {
			case C_ARRAY :
				return appendArrayTypeSignature(string, start, fullyQualifyTypeNames, buffer, true);
			case C_RESOLVED :
			case C_UNRESOLVED :
			case C_TYPE_VARIABLE :
			case C_BOOLEAN :
			case C_BYTE :
			case C_CHAR :
			case C_DOUBLE :
			case C_FLOAT :
			case C_INT :
			case C_LONG :
			case C_SHORT :
			case C_VOID :
			case C_STAR:
			case C_EXTENDS:
			case C_SUPER:
			case C_CAPTURE:
			default:
				throw new IllegalArgumentException(); // a var args is an array type
		}
	} else {
		switch (c) {
			case C_ARRAY :
				return appendArrayTypeSignature(string, start, fullyQualifyTypeNames, buffer);
			case C_RESOLVED :
			case C_UNRESOLVED :
				return appendClassTypeSignature(string, start, fullyQualifyTypeNames, buffer);
			case C_TYPE_VARIABLE :
				int e = Util.scanTypeVariableSignature(string, start);
				buffer.append(string, start + 1, e - start - 1);
				return e;
			case C_BOOLEAN :
				buffer.append(BOOLEAN);
				return start;
			case C_BYTE :
				buffer.append(BYTE);
				return start;
			case C_CHAR :
				buffer.append(CHAR);
				return start;
			case C_DOUBLE :
				buffer.append(DOUBLE);
				return start;
			case C_FLOAT :
				buffer.append(FLOAT);
				return start;
			case C_INT :
				buffer.append(INT);
				return start;
			case C_LONG :
				buffer.append(LONG);
				return start;
			case C_SHORT :
				buffer.append(SHORT);
				return start;
			case C_VOID :
				buffer.append(VOID);
				return start;
			case C_CAPTURE :
				return appendCaptureTypeSignature(string, start, fullyQualifyTypeNames, buffer);
			case C_STAR:
			case C_EXTENDS:
			case C_SUPER:
				return appendTypeArgumentSignature(string, start, fullyQualifyTypeNames, buffer);
			default :
				throw new IllegalArgumentException();
		}
	}
}
/**
 * Scans the given string for an array type signature starting at the given
 * index and appends it to the given buffer, and returns the index of the last
 * character.
 * 
 * @param string the signature string
 * @param start the 0-based character index of the first character
 * @param fullyQualifyTypeNames <code>true</code> if type names should be fully
 *   qualified, and <code>false</code> to use only simple names
 * @return the 0-based character index of the last character
 * @exception IllegalArgumentException if this is not an array type signature
 * @see Util#scanArrayTypeSignature(char[], int)
 */
private static int appendArrayTypeSignature(char[] string, int start, boolean fullyQualifyTypeNames, StringBuffer buffer) {
	return appendArrayTypeSignature(string, start, fullyQualifyTypeNames, buffer, false);
}
/**
 * Scans the given string for an array type signature starting at the given
 * index and appends it to the given buffer, and returns the index of the last
 * character.
 * 
 * @param string the signature string
 * @param start the 0-based character index of the first character
 * @param fullyQualifyTypeNames <code>true</code> if type names should be fully
 *   qualified, and <code>false</code> to use only simple names
 * @return the 0-based character index of the last character
 * @exception IllegalArgumentException if this is not an array type signature
 * @see Util#scanArrayTypeSignature(char[], int)
 */
private static int appendCaptureTypeSignature(char[] string, int start, boolean fullyQualifyTypeNames, StringBuffer buffer) {
	// need a minimum 2 char
	if (start >= string.length - 1) {
		throw new IllegalArgumentException();
	}
	char c = string[start];
	if (c != C_CAPTURE) {
		throw new IllegalArgumentException();
	}
	buffer.append(CAPTURE).append(' ');
	return appendTypeArgumentSignature(string, start + 1, fullyQualifyTypeNames, buffer);
}

/**
 * Scans the given string for an array type signature starting at the given
 * index and appends it to the given buffer, and returns the index of the last
 * character.
 * 
 * @param string the signature string
 * @param start the 0-based character index of the first character
 * @param fullyQualifyTypeNames <code>true</code> if type names should be fully
 *   qualified, and <code>false</code> to use only simple names
 * @param isVarArgs <code>true</code> if the array type must be displayed as a
 * variable argument, <code>false</code> otherwise
 * @return the 0-based character index of the last character
 * @exception IllegalArgumentException if this is not an array type signature
 * @see Util#scanArrayTypeSignature(char[], int)
 */
private static int appendArrayTypeSignature(char[] string, int start, boolean fullyQualifyTypeNames, StringBuffer buffer, boolean isVarArgs) {
	int length = string.length;
	// need a minimum 2 char
	if (start >= length - 1) {
		throw new IllegalArgumentException();
	}
	char c = string[start];
	if (c != C_ARRAY) {
		throw new IllegalArgumentException();
	}
	
	int index = start;
	c = string[++index];
	while(c == C_ARRAY) {
		// need a minimum 2 char
		if (index >= length - 1) {
			throw new IllegalArgumentException();
		}
		c = string[++index];
	}
	
	int e = appendTypeSignature(string, index, fullyQualifyTypeNames, buffer);
	
	for(int i = 1, dims = index - start; i < dims; i++) {
		buffer.append('[').append(']');
	}
	
	if (isVarArgs) {
		buffer.append('.').append('.').append('.');
	} else {
		buffer.append('[').append(']');
	}
	return e;
}
/**
 * Scans the given string for a class type signature starting at the given
 * index and appends it to the given buffer, and returns the index of the last
 * character.
 * 
 * @param string the signature string
 * @param start the 0-based character index of the first character
 * @param fullyQualifyTypeNames <code>true</code> if type names should be fully
 *   qualified, and <code>false</code> to use only simple names
 * @param buffer the string buffer to append to
 * @return the 0-based character index of the last character
 * @exception IllegalArgumentException if this is not a class type signature
 * @see Util#scanClassTypeSignature(char[], int)
 */
private static int appendClassTypeSignature(char[] string, int start, boolean fullyQualifyTypeNames, StringBuffer buffer) {
	// need a minimum 3 chars "Lx;"
	if (start >= string.length - 2) { 
		throw new IllegalArgumentException();
	}
	// must start in "L" or "Q"
	char c = string[start];
	if (c != C_RESOLVED && c != C_UNRESOLVED) {
		throw new IllegalArgumentException();
	}
	boolean resolved = (c == C_RESOLVED);
	boolean removePackageQualifiers = !fullyQualifyTypeNames;
	if (!resolved) {
		// keep everything in an unresolved name
		removePackageQualifiers = false;
	}
	int p = start + 1;
	int checkpoint = buffer.length();
	while (true) {
		if (p >= string.length) {
			throw new IllegalArgumentException();
		}
		c = string[p];
		switch(c) {
			case C_SEMICOLON :
				// all done
				return p;
			case C_GENERIC_START :
				int e = appendTypeArgumentSignatures(string, p, fullyQualifyTypeNames, buffer);
				// once we hit type arguments there are no more package prefixes
				removePackageQualifiers = false;
				p = e;
				break;
			case C_DOT :
				if (removePackageQualifiers) {
					// erase package prefix
					buffer.setLength(checkpoint);
				} else {
					buffer.append('.');
				}
				break;
			 case '/' :
				if (removePackageQualifiers) {
					// erase package prefix
					buffer.setLength(checkpoint);
				} else {
					buffer.append('/');
				}
				break;
			 case C_DOLLAR :
			 	if (resolved) {
					// once we hit "$" there are no more package prefixes
					removePackageQualifiers = false;
					/**
					 * Convert '$' in resolved type signatures into '.'.
					 * NOTE: This assumes that the type signature is an inner type
					 * signature. This is true in most cases, but someone can define a
					 * non-inner type name containing a '$'.
					 */
					buffer.append('.');
			 	}
			 	break;
			 default :
				buffer.append(c);
		}
		p++;
	}
}

/**
 * Scans the given string for a list of type arguments signature starting at the
 * given index and appends it to the given buffer, and returns the index of the
 * last character.
 * 
 * @param string the signature string
 * @param start the 0-based character index of the first character
 * @param fullyQualifyTypeNames <code>true</code> if type names should be fully
 *   qualified, and <code>false</code> to use only simple names
 * @param buffer the string buffer to append to
 * @return the 0-based character index of the last character
 * @exception IllegalArgumentException if this is not a list of type argument
 * signatures
 * @see Util#scanTypeArgumentSignatures(char[], int)
 */
private static int appendTypeArgumentSignatures(char[] string, int start, boolean fullyQualifyTypeNames, StringBuffer buffer) {
	// need a minimum 2 char "<>"
	if (start >= string.length - 1) {
		throw new IllegalArgumentException();
	}
	char c = string[start];
	if (c != C_GENERIC_START) {
		throw new IllegalArgumentException();
	}
	buffer.append('<');
	int p = start + 1;
	int count = 0;
	while (true) {
		if (p >= string.length) {
			throw new IllegalArgumentException();
		}
		c = string[p];
		if (c == C_GENERIC_END) {
			buffer.append('>');
			return p;
		}
		if (count != 0) {
			buffer.append(',');
		}
		int e = appendTypeArgumentSignature(string, p, fullyQualifyTypeNames, buffer);
		count++;
		p = e + 1;
	}
}

/**
 * Scans the given string for a type argument signature starting at the given
 * index and appends it to the given buffer, and returns the index of the last
 * character.
 * 
 * @param string the signature string
 * @param start the 0-based character index of the first character
 * @param fullyQualifyTypeNames <code>true</code> if type names should be fully
 *   qualified, and <code>false</code> to use only simple names
 * @param buffer the string buffer to append to
 * @return the 0-based character index of the last character
 * @exception IllegalArgumentException if this is not a type argument signature
 * @see Util#scanTypeArgumentSignature(char[], int)
 */
private static int appendTypeArgumentSignature(char[] string, int start, boolean fullyQualifyTypeNames, StringBuffer buffer) {
	// need a minimum 1 char
	if (start >= string.length) {
		throw new IllegalArgumentException();
	}
	char c = string[start];
	switch(c) {
		case C_STAR :
			buffer.append('?');
			return start;
		case C_EXTENDS :
			buffer.append("? extends "); //$NON-NLS-1$
			return appendTypeSignature(string, start + 1, fullyQualifyTypeNames, buffer);
		case C_SUPER :
			buffer.append("? super "); //$NON-NLS-1$
			return appendTypeSignature(string, start + 1, fullyQualifyTypeNames, buffer);
		default :
			return appendTypeSignature(string, start, fullyQualifyTypeNames, buffer);
	}
}

/**
 * Converts the given array of qualified name segments to a qualified name.
 * <p>
 * For example:
 * <pre>
 * <code>
 * toQualifiedName({{'j', 'a', 'v', 'a'}, {'l', 'a', 'n', 'g'}, {'O', 'b', 'j', 'e', 'c', 't'}}) -> {'j', 'a', 'v', 'a', '.', 'l', 'a', 'n', 'g', '.', 'O', 'b', 'j', 'e', 'c', 't'}
 * toQualifiedName({{'O', 'b', 'j', 'e', 'c', 't'}}) -> {'O', 'b', 'j', 'e', 'c', 't'}
 * toQualifiedName({{}}) -> {}
 * </code>
 * </pre>
 * </p>
 *
 * @param segments the list of name segments, possibly empty
 * @return the dot-separated qualified name, or the empty string
 * 
 * @since 2.0
 */
public static char[] toQualifiedName(char[][] segments) {
	int length = segments.length;
	if (length == 0) return CharOperation.NO_CHAR;
	if (length == 1) return segments[0];
	
	int resultLength = 0;
	for (int i = 0; i < length; i++) {
		resultLength += segments[i].length+1;
	}
	resultLength--;
	char[] result = new char[resultLength];
	int index = 0;
	for (int i = 0; i < length; i++) {
		char[] segment = segments[i];
		int segmentLength = segment.length;
		System.arraycopy(segment, 0, result, index, segmentLength);
		index += segmentLength;
		if (i != length-1) {
			result[index++] = C_DOT;
		}
	}
	return result;
}
/**
 * Converts the given array of qualified name segments to a qualified name.
 * <p>
 * For example:
 * <pre>
 * <code>
 * toQualifiedName(new String[] {"java", "lang", "Object"}) -> "java.lang.Object"
 * toQualifiedName(new String[] {"Object"}) -> "Object"
 * toQualifiedName(new String[0]) -> ""
 * </code>
 * </pre>
 * </p>
 *
 * @param segments the list of name segments, possibly empty
 * @return the dot-separated qualified name, or the empty string
 */
public static String toQualifiedName(String[] segments) {
	int length = segments.length;
	char[][] charArrays = new char[length][];
	for (int i = 0; i < length; i++) {
		charArrays[i] = segments[i].toCharArray();
	}
	return new String(toQualifiedName(charArrays));
}
/**
 * Converts the given type signature to a readable string. The signature is expected to
 * be dot-based.
 * 
 * <p>
 * For example:
 * <pre>
 * <code>
 * toString("[Ljava.lang.String;") -> "java.lang.String[]"
 * toString("I") -> "int"
 * toString("+QObject;") -> "? extends Object"
 * </code>
 * </pre>
 * </p>
 * <p>
 * Note: This method assumes that a type signature containing a <code>'$'</code>
 * is an inner type signature. While this is correct in most cases, someone could 
 * define a non-inner type name containing a <code>'$'</code>. Handling this 
 * correctly in all cases would have required resolving the signature, which 
 * generally not feasible.
 * </p>
 *
 * @param signature the type signature
 * @return the string representation of the type
 * @exception IllegalArgumentException if the signature is not syntactically
 *   correct
 */
public static String toString(String signature) throws IllegalArgumentException {
	ToCharArraySignatureRequestor requestor = new ToCharArraySignatureRequestor(true);
	requestor.forFunction = false;
	
	try {
		SignatureProcessor.process(signature, requestor);
	} catch (Exception e) {
		return "";
	}
	
	if (requestor.stack.isEmpty()) {
		return "";
	}
	
	Stack<String> stack = requestor.stack.pop();
	if (stack.isEmpty()) {
		return "";
	}
	
	return stack.pop();
}
/**
 * Converts the given method signature to a readable string. The method signature is expected to
 * be dot-based.
 * 
 * @param methodSignature the method signature to convert
 * @param methodName the name of the method to insert in the result, or 
 *   <code>null</code> if no method name is to be included
 * @param parameterNames the parameter names to insert in the result, or 
 *   <code>null</code> if no parameter names are to be included; if supplied,
 *   the number of parameter names must match that of the method signature
 * @param fullyQualifyTypeNames <code>true</code> if type names should be fully
 *   qualified, and <code>false</code> to use only simple names
 * @param includeReturnType <code>true</code> if the return type is to be
 *   included
 * @see #toCharArray(char[], char[], char[][], boolean, boolean)
 * @return the string representation of the method signature
 */
public static String toString(String methodSignature, String methodName, String[] parameterNames, boolean fullyQualifyTypeNames, boolean includeReturnType) {
	return toString(methodSignature, methodName, parameterNames, fullyQualifyTypeNames, includeReturnType, false);
}
/**
 * Converts the given method signature to a readable string. The method signature is expected to
 * be dot-based.
 * 
 * @param methodSignature the method signature to convert
 * @param methodName the name of the method to insert in the result, or 
 *   <code>null</code> if no method name is to be included
 * @param parameterNames the parameter names to insert in the result, or 
 *   <code>null</code> if no parameter names are to be included; if supplied,
 *   the number of parameter names must match that of the method signature
 * @param fullyQualifyTypeNames <code>true</code> if type names should be fully
 *   qualified, and <code>false</code> to use only simple names
 * @param includeReturnType <code>true</code> if the return type is to be
 *   included
 * @param isVarArgs <code>true</code> if the last argument should be displayed as a 
 * variable argument, <code>false</code> otherwise
 * @see #toCharArray(char[], char[], char[][], boolean, boolean)
 * @return the string representation of the method signature
 *
 * @since 3.1
 */
public static String toString(String methodSignature, String methodName, String[] parameterNames, boolean fullyQualifyTypeNames, boolean includeReturnType, boolean isVarArgs) {
	char[][] params;
	if (parameterNames == null) {
		params = null;
	} else {
		int paramLength = parameterNames.length;
		params = new char[paramLength][];
		for (int i = 0; i < paramLength; i++) {
			params[i] = parameterNames[i].toCharArray();
		}
	}
	return new String(toCharArray(methodSignature.toCharArray(), methodName == null ? null : methodName.toCharArray(), params, fullyQualifyTypeNames, includeReturnType, isVarArgs));
}
}
