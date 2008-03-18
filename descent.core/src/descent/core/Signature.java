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
import java.util.List;
import java.util.Stack;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.core.SignatureProcessor;
import descent.internal.core.SignatureRequestorAdapter;


/**
 * Provides methods for encoding and decoding type signature strings.
 * <p>
 * Signatures are returned in various points from the D model, like
 * {@link IType#getSuperclassTypeSignature()}, {@link IField#getTypeSignature()},
 * etc.
 * </p>
 * <p>
 * Note that these signatures differ substantially from the signatures used by
 * an underlying compiler such as DMD and GDC: they are specific to Descent. 
 * </p>
 * <p>
 * The syntax for a type signature is:
 * <pre>
 * TypeSignature ::=
 *   | PrimitiveTypeSignature
 *   | PointerTypeSignature   
 *   | DynamicArrayTypeSignature
 *   | StaticArrayTypeSignature
 *   | AssociativeArrayTypeSignature
 *   | TypeofTypeSignature
 *   | SliceTypeSignature
 *   | FunctionTypeSignature
 *   | DelegateTypeSignature
 *   | IdentifierTypeSignature
 *   | SymbolTypeSignature
 *   
 * PrimitiveTypeSignature ::=
 *     "v"  // void
 *   | "g"  // byte
 *   | "h"  // ubyte
 *   | "s"  // short
 *   | "t"  // ushort
 *   | "i"  // int
 *   | "k"  // uint
 *   | "l"  // long
 *   | "m"  // ulong
 *   | "f"  // float
 *   | "d"  // double
 *   | "e"  // real
 *   | "o"  // ifloat
 *   | "p"  // idouble
 *   | "j"  // ireal
 *   | "q"  // cfloat
 *   | "r"  // cdouble
 *   | "c"  // creal
 *   | "b"  // bool
 *   | "a"  // char
 *   | "u"  // wchar
 *   | "w"  // dchar
 *   
 * PointerTypeSignature ::=
 *   "P" TypeSignature
 *   
 * DynamicArrayTypeSignature ::=
 *   "A" TypeSignature
 *     
 * StaticArrayTypeSignature ::=
 *   "G" TypeSignature Number "G" Chars // Number == Chars.length
 *   
 * AssociativeArrayTypeSignature ::=
 *   "H" TypeSignature TypeSignature  // key value
 *   
 * TypeofTypeSignature ::=
 *   ">" Number ">" Chars // Number == Chars.length
 *   
 * SliceTypeSignature ::=
 *   "¬" TypeSignature
 *   Number "¬" Chars // Number == Chars.length --> lower
 *   Number "¬" Chars // Number == Chars.length --> upper
 *   
 * FunctionTypeSignature ::=
 *   ( "F"  // D linkage
 *   | "U"  // C linkage
 *   | "W"  // Windows linkage
 *   | "V"  // Pascal linkage
 *   | "R"  // C++ linkage
 *   )
 *   ( TypeSignature )*  // parameter types
 *   ( "X"
 *   | "Y"
 *   | "Z"
 *   )
 *   TypeSignature  // return type
 *   
 * DelegateTypeSignature ::=
 *   "D" FunctionTypeSignature
 *   
 * IdentifierTypeSignature ::=
 *   "?" ( Identifier )+ ( TemplateInstance )?
 *   
 * SymbolTypeSignature ::=
 *   ( ModuleSignature )?
 *   ( 
 *     ( "C"  // class
 *     | "S"  // struct
 *     | "&"  // union
 *     | "|"  // interface
 *     )
 *     Identifier ( TemplateInstance )?
 *   |
 *     ( "{"  // template
 *     | "&lt;"  // templated class
 *     | ";"     // templated struct
 *     | ":"     // templated union
 *     | "("     // templated interface
 *     )
 *     Identifier TemplateParameters ( TemplateInstance )?
 *   |
 *     ")"  // templated function
 *     Identifier FunctionTypeSignature TemplateParameters
 *   )*
 *   
 * ModuleSignature ::=
 *   "@" ( Identifier )*
 *   
 * TemplateParameters ::=
 *   ( TemplateParameter )* "\"
 *   
 * TemplateParameter ::=
 *     TemplateTupleParameter
 *   | TemplateAliasParameter
 *   | TemplateTypeParameter
 *   | TemplateValueParameter
 *   
 * TemplateTupleParameter ::=
 *   "%"
 *   
 * TemplateAliasParameter ::=
 *   "]"
 *   ( '"' TypeSignature )?  // specifc type
 *   
 * TemplateTypeParameter ::=
 *   "#"
 *   ( "+" TypeSignature )?  // specifc type
 *   
 * TemplateValueParameter ::=
 *   ","
 *   TypeSignature
 *   ( Number "," Chars )?  // specific value, Number == Chars.length
 *   
 * TemplateInstance ::=
 *   "!" TemplateInstanceParameters
 *   
 * TemplateInstanceParameters ::=
 *   ( TemplateInstanceParameter )* "\"
 *   
 * TemplateInstanceParameter ::=
 *     TemplateInstanceTypeParameter
 *   | TemplateInstanceValueParameter
 *   | TemplateInstanceSymbolParameter
 *   
 * TemplateInstanceTypeParameter ::=
 *   "^" TypeSignature
 *   
 * TemplateInstanceValueParameter ::=
 *   "-"
 *   Number "-" Chars  // Number == Chars.length
 *   
 * TemplateInstanceSymbolParameter ::=
 *   "*" TypeSignature
 * 
 * Identifier ::=
 *   Number Chars  // Number == Chars.length
 * 
 * Chars ::=
 *   'any sequence of characters'
 *   
 * Number ::= 
 *   ( 0 | .. | 9 )+
 *   
 * </pre>
 * </p>
 * <p>
 * Examples:
 * <ul>
 *   <li><code>"i"</code> denotes <code>int</code></li>
 *   <li><code>"Pv"</code> denotes <code>void*</code></li>
 *   <li><code>"Ga2G80"</code> denotes <code>char[80]</code></li>
 *   <li><code>"FwZb"</code> denotes <code>bool function(wchar)</code></li>
 *   <li><code>"@4test3fooC3Bar"</code> denotes the class <code>test.foo.Bar</code></li>
 * </ul>
 * </p>
 * <p>
 * This class provides static methods and constants only; it is not intended to be
 * instantiated or subclassed by clients.
 * </p>
 */
public final class Signature {
	
	/**
	 * Character constant indicating the primitive type void in a signature.
	 * Value is <code>'v'</code>.
	 */
	public static final char C_VOID										= 'v';
	
	/**
	 * Character constant indicating the primitive type byte in a signature.
	 * Value is <code>'g'</code>.
	 */
	public static final char C_BYTE 									= 'g';
	
	/**
	 * Character constant indicating the primitive type ubyte in a signature.
	 * Value is <code>'h'</code>.
	 */
	public static final char C_UBYTE 									= 'h';
	
	/**
	 * Character constant indicating the primitive type short in a signature.
	 * Value is <code>'s'</code>.
	 */
	public static final char C_SHORT 									= 's';
	
	/**
	 * Character constant indicating the primitive type ushort in a signature.
	 * Value is <code>'t'</code>.
	 */
	public static final char C_USHORT 									= 't';
	
	/**
	 * Character constant indicating the primitive type int in a signature.
	 * Value is <code>'i'</code>.
	 */
	public static final char C_INT	 									= 'i';
	
	/**
	 * Character constant indicating the primitive type uint in a signature.
	 * Value is <code>'k'</code>.
	 */
	public static final char C_UINT 									= 'k';
	
	/**
	 * Character constant indicating the primitive type long in a signature.
	 * Value is <code>'l'</code>.
	 */
	public static final char C_LONG	 									= 'l';
	
	/**
	 * Character constant indicating the primitive type ulong in a signature.
	 * Value is <code>'m'</code>.
	 */
	public static final char C_ULONG 									= 'm';
	
	/**
	 * Character constant indicating the primitive type float in a signature.
	 * Value is <code>'f'</code>.
	 */
	public static final char C_FLOAT 									= 'f';
	
	/**
	 * Character constant indicating the primitive type double in a signature.
	 * Value is <code>'d'</code>.
	 */
	public static final char C_DOUBLE 									= 'd';
	
	/**
	 * Character constant indicating the primitive type real in a signature.
	 * Value is <code>'e'</code>.
	 */
	public static final char C_REAL 									= 'e';
	
	/**
	 * Character constant indicating the primitive type ifloat in a signature.
	 * Value is <code>'o'</code>.
	 */
	public static final char C_IFLOAT 									= 'o';
	
	/**
	 * Character constant indicating the primitive type idouble in a signature.
	 * Value is <code>'p'</code>.
	 */
	public static final char C_IDOUBLE 									= 'p';
	
	/**
	 * Character constant indicating the primitive type ireal in a signature.
	 * Value is <code>'j'</code>.
	 */
	public static final char C_IREAL 									= 'j';
	
	/**
	 * Character constant indicating the primitive type cfloat in a signature.
	 * Value is <code>'q'</code>.
	 */
	public static final char C_CFLOAT 									= 'q';
	
	/**
	 * Character constant indicating the primitive type cdouble in a signature.
	 * Value is <code>'r'</code>.
	 */
	public static final char C_CDOUBLE 									= 'r';
	
	/**
	 * Character constant indicating the primitive type creal in a signature.
	 * Value is <code>'c'</code>.
	 */
	public static final char C_CREAL 									= 'c';

	/**
	 * Character constant indicating the primitive type boolean in a signature.
	 * Value is <code>'b'</code>.
	 */
	public static final char C_BOOL 									= 'b';
	
	/**
	 * Character constant indicating the primitive type char in a signature.
	 * Value is <code>'a'</code>.
	 */
	public static final char C_CHAR 									= 'a';
	
	/**
	 * Character constant indicating the primitive type wchar in a signature.
	 * Value is <code>'u'</code>.
	 */
	public static final char C_WCHAR 									= 'u';
	
	/**
	 * Character constant indicating the primitive type dchar in a signature.
	 * Value is <code>'w'</code>.
	 */
	public static final char C_DCHAR 									= 'w';
	
	/**
	 * Character constant indicating a pointer type in a signature.
	 * Value is <code>'P'</code>.
	 */
	public static final char C_POINTER 									= 'P';
	
	/**
	 * Character constant indicating a dynamic array type in a signature.
	 * Value is <code>'A'</code>.
	 */
	public static final char C_DYNAMIC_ARRAY							= 'A';
	
	/**
	 * Character constant indicating a static array type in a signature.
	 * Value is <code>'G'</code>.
	 */
	public static final char C_STATIC_ARRAY								= 'G';
	
	/**
	 * Character constant indicating an associative array type in a signature.
	 * Value is <code>'H'</code>.
	 */
	public static final char C_ASSOCIATIVE_ARRAY						= 'H';
	
	/**
	 * Character constant indicating a typeof type in a signature.
	 * Value is <code>'>'</code>.
	 */
	public static final char C_TYPEOF									= '>';
	
	/**
	 * Character constant indicating a slice type in a signature.
	 * Value is <code>'¬'</code>.
	 */
	public static final char C_SLICE									= '¬';
	
	/**
	 * Character constant indicating a D linkage in a function signature.
	 * Value is <code>'F'</code>.
	 */
	public static final char C_D_LINKAGE								= 'F';
	
	/**
	 * Character constant indicating a C linkage in a function signature.
	 * Value is <code>'U'</code>.
	 */
	public static final char C_C_LINKAGE								= 'U';
	
	/**
	 * Character constant indicating a Windows linkage in a function signature.
	 * Value is <code>'W'</code>.
	 */
	public static final char C_WINDOWS_LINKAGE								= 'W';
	
	/**
	 * Character constant indicating a Pascal linkage in a function signature.
	 * Value is <code>'V'</code>.
	 */
	public static final char C_PASCAL_LINKAGE								= 'V';
	
	/**
	 * Character constant indicating a C++ linkage in a function signature.
	 * Value is <code>'R'</code>.
	 */
	public static final char C_CPP_LINKAGE								= 'R';
	
	/**
	 * Character constant indicating a function parameters break using the X letter.
	 * Value is <code>'X'</code>.
	 */
	public static final char C_FUNCTION_PARAMTERS_BREAK_1				= 'X';
	
	/**
	 * Character constant indicating a function parameters break using the Y letter.
	 * Value is <code>'Y'</code>.
	 */
	public static final char C_FUNCTION_PARAMTERS_BREAK_2				= 'Y';
	
	/**
	 * Character constant indicating a function parameters break using the Z letter.
	 * Value is <code>'Z'</code>.
	 */
	public static final char C_FUNCTION_PARAMTERS_BREAK_3				= 'Z';
	
	/**
	 * Character constant indicating a delegate type in a signature.
	 * Value is <code>'D'</code>.
	 */
	public static final char C_DELEGATE									= 'D';
	
	/**
	 * Character constant indicating an identifier type in a signature.
	 * Value is <code>'I'</code>.
	 */
	public static final char C_IDENTIFIER								= '?';
	
	/**
	 * Character constant indicating a class type in a signature.
	 * Value is <code>'C'</code>.
	 */
	public static final char C_CLASS									= 'C';
	
	/**
	 * Character constant indicating a struct type in a signature.
	 * Value is <code>'S'</code>.
	 */
	public static final char C_STRUCT									= 'S';
	
	/**
	 * Character constant indicating a union type in a signature.
	 * Value is <code>'&'</code>.
	 */
	public static final char C_UNION									= '&';
	
	/**
	 * Character constant indicating an interface type in a signature.
	 * Value is <code>'|'</code>.
	 */
	public static final char C_INTERFACE								= '|';
	
	/**
	 * Character constant indicating a template type in a signature.
	 * Value is <code>'I'</code>.
	 */
	public static final char C_TEMPLATE									= '{';
	
	/**
	 * Character constant indicating a templated class in a signature.
	 * Value is <code>'<'</code>.
	 */
	public static final char C_TEMPLATED_CLASS							= '<';
	
	/**
	 * Character constant indicating a templated struct in a signature.
	 * Value is <code>';'</code>.
	 */
	public static final char C_TEMPLATED_STRUCT							= ';';
	
	/**
	 * Character constant indicating a templated union in a signature.
	 * Value is <code>':'</code>.
	 */
	public static final char C_TEMPLATED_UNION							= ':';
	
	/**
	 * Character constant indicating a templated interface in a signature.
	 * Value is <code>'('</code>.
	 */
	public static final char C_TEMPLATED_INTERFACE						= '(';
	
	/**
	 * Character constant indicating a templated function in a signature.
	 * Value is <code>')'</code>.
	 */
	public static final char C_TEMPLATED_FUNCTION						= ')';
	
	/**
	 * Character constant indicating a module in a signature.
	 * Value is <code>'@'</code>.
	 */
	public static final char C_MODULE									= '@';
	
	/**
	 * Character constant indicating a template parameters break in a signature.
	 * Value is <code>'\'</code>.
	 */
	public static final char C_TEMPLATE_PARAMETERS_BREAK				= '\\';
	
	/**
	 * Character constant indicating a template tuple parameter in a signature.
	 * Value is <code>'%'</code>.
	 */
	public static final char C_TEMPLATE_TUPLE_PARAMETER					= '%';
	
	/**
	 * Character constant indicating a template alias parameter in a signature.
	 * Value is <code>']'</code>.
	 */
	public static final char C_TEMPLATE_ALIAS_PARAMETER					= ']';
	
	/**
	 * Character constant indicating a template alias parameter specific type in a signature.
	 * Value is <code>'"'</code>.
	 */
	public static final char C_TEMPLATE_ALIAS_PARAMETER_SPECIFIC_TYPE	= '"';
	
	/**
	 * Character constant indicating a template type parameter in a signature.
	 * Value is <code>'#'</code>.
	 */
	public static final char C_TEMPLATE_TYPE_PARAMETER					= '#';
	
	/**
	 * Character constant indicating a template type parameter specific type in a signature.
	 * Value is <code>'+'</code>.
	 */
	public static final char C_TEMPLATE_TYPE_PARAMETER_SPECIFIC_TYPE	= '+';
	
	/**
	 * Character constant indicating a template value parameter in a signature.
	 * Value is <code>','</code>.
	 */
	public static final char C_TEMPLATE_VALUE_PARAMETER					= ',';
	
	/**
	 * Character constant indicating a template instance in a signature.
	 * Value is <code>'!'</code>.
	 */
	public static final char C_TEMPLATE_INSTANCE						= '!';
	
	/**
	 * Character constant indicating a template instance type parameter in a signature.
	 * Value is <code>'^'</code>.
	 */
	public static final char C_TEMPLATE_INSTANCE_TYPE_PARAMETER			= '^';
	
	/**
	 * Character constant indicating a template instance value parameter in a signature.
	 * Value is <code>'-'</code>.
	 */
	public static final char C_TEMPLATE_INSTANCE_VALUE_PARAMETER		= '-';
	
	/**
	 * Character constant indicating a template instance symbol parameter in a signature.
	 * Value is <code>'*'</code>.
	 */
	public static final char C_TEMPLATE_INSTANCE_SYMBOL_PARAMETER		= '*';
	
	/**
	 * String constant for the signature of the primitive type void.
	 * Value is <code>"v"</code>.
	 */
	public static final String SIG_VOID									= "v";
	
	/**
	 * String constant for the signature of the primitive type byte.
	 * Value is <code>"g"</code>.
	 */
	public static final String SIG_BYTE									= "g";
	
	/**
	 * String constant for the signature of the primitive type ubyte.
	 * Value is <code>"h"</code>.
	 */
	public static final String SIG_UBYTE								= "h";
	
	/**
	 * String constant for the signature of the primitive type short.
	 * Value is <code>"s"</code>.
	 */
	public static final String SIG_SHORT								= "s";
	
	/**
	 * String constant for the signature of the primitive type ushort.
	 * Value is <code>"t"</code>.
	 */
	public static final String SIG_USHORT								= "t";
	
	/**
	 * String constant for the signature of the primitive type int.
	 * Value is <code>"i"</code>.
	 */
	public static final String SIG_INT									= "i";
	
	/**
	 * String constant for the signature of the primitive type uint.
	 * Value is <code>"k"</code>.
	 */
	public static final String SIG_UINT									= "k";
	
	/**
	 * String constant for the signature of the primitive type long.
	 * Value is <code>"l"</code>.
	 */
	public static final String SIG_LONG									= "l";
	
	/**
	 * String constant for the signature of the primitive type ulong.
	 * Value is <code>"m"</code>.
	 */
	public static final String SIG_ULONG								= "m";
	
	/**
	 * String constant for the signature of the primitive type float.
	 * Value is <code>"f"</code>.
	 */
	public static final String SIG_FLOAT								= "f";
	
	/**
	 * String constant for the signature of the primitive type double.
	 * Value is <code>"d"</code>.
	 */
	public static final String SIG_DOUBLE								= "d";
	
	/**
	 * String constant for the signature of the primitive type real.
	 * Value is <code>"e"</code>.
	 */
	public static final String SIG_REAL									= "e";
	
	/**
	 * String constant for the signature of the primitive type ifloat.
	 * Value is <code>"o"</code>.
	 */
	public static final String SIG_IFLOAT								= "o";
	
	/**
	 * String constant for the signature of the primitive type idouble.
	 * Value is <code>"p"</code>.
	 */
	public static final String SIG_IDOUBLE								= "p";
	
	/**
	 * String constant for the signature of the primitive type ireal.
	 * Value is <code>"j"</code>.
	 */
	public static final String SIG_IREAL								= "j";
	
	/**
	 * String constant for the signature of the primitive type cfloat.
	 * Value is <code>"q"</code>.
	 */
	public static final String SIG_CFLOAT								= "q";
	
	/**
	 * String constant for the signature of the primitive type cdouble.
	 * Value is <code>"r"</code>.
	 */
	public static final String SIG_CDOUBLE								= "r";
	
	/**
	 * String constant for the signature of the primitive type creal.
	 * Value is <code>"c"</code>.
	 */
	public static final String SIG_CREAL								= "c";

	/**
	 * String constant for the signature of the primitive type bool.
	 * Value is <code>"b"</code>.
	 */
	public static final String SIG_BOOL									= "b";
	
	/**
	 * String constant for the signature of the primitive type char.
	 * Value is <code>"a"</code>.
	 */
	public static final String SIG_CHAR									= "a";
	
	/**
	 * String constant for the signature of the primitive type wchar.
	 * Value is <code>"u"</code>.
	 */
	public static final String SIG_WCHAR								= "u";
	
	/**
	 * String constant for the signature of the primitive type dchar.
	 * Value is <code>"w"</code>.
	 */
	public static final String SIG_DCHAR								= "w";
		
private Signature() {
	// Not instantiable
}

/**
 * Creates a signature for a pointer that points to the given type.
 * @param type the type pointed by the pointer
 * @return the pointer signature
 */
public static String createPointerSignature(String type) {
	StringBuilder sb = new StringBuilder();
	sb.append(C_POINTER);
	sb.append(type);
	return sb.toString();
}

/**
 * Creates a signature for a pointer that points to the given type.
 * @param type the type pointed by the pointer
 * @return the pointer signature
 */
public static char[] createPointerSignature(char[] type) {
	char[] ret = new char[1 + type.length];
	ret[0] = C_POINTER;
	System.arraycopy(type, 0, ret, 1, type.length);
	return ret;
}

/**
 * Creates a static array signature with the given type and dimension.
 * @param type the type of the static array
 * @param dimension the dimension of the static array
 * @return the static array signature
 */
public static String createStaticArraySignature(String type, String dimension) {
	StringBuilder sb = new StringBuilder();
	sb.append(C_STATIC_ARRAY);
	sb.append(type);
	sb.append(dimension.length());
	sb.append(C_STATIC_ARRAY);
	sb.append(dimension);
	return sb.toString();
}

/**
 * Creates a static array signature with the given type and dimension.
 * @param type the type of the static array
 * @param dimension the dimension of the static array
 * @return the static array signature
 */
public static char[] createStaticArraySignature(char[] type, char[] dimension) {
	int dimensionLength = dimension.length;
	int dimensionLengthStringLength = lenghtOfLengthToString(dimension);
	
	char[] ret = new char[2 + type.length + dimensionLengthStringLength + dimensionLength];
	ret[0] = C_STATIC_ARRAY;
	System.arraycopy(type, 0, ret, 1, type.length);
	copyNumber(dimensionLength, dimensionLengthStringLength, ret, 1 + type.length);
	ret[1 + type.length + dimensionLengthStringLength] = C_STATIC_ARRAY;
	System.arraycopy(dimension, 0, ret, 2 + type.length + dimensionLengthStringLength, dimensionLength);
	return ret;
}

/**
 * Creates a dynamic array signature with the given type.
 * @param type the type of the dynamic array
 * @return the dynamic array signature
 */
public static String createDynamicArraySignature(String type) {
	StringBuilder sb = new StringBuilder();
	sb.append(C_DYNAMIC_ARRAY);
	sb.append(type);
	return sb.toString();
}

/**
 * Creates a dynamic array signature with the given type.
 * @param type the type of the dynamic array
 * @return the dynamic array signature
 */
public static char[] createDynamicArraySignature(char[] type) {
	char[] ret = new char[1 + type.length];
	ret[0] = C_DYNAMIC_ARRAY;
	System.arraycopy(type, 0, ret, 1, type.length);
	return ret;
}

/**
 * Creates an associative array signature with the given key and value
 * signatures.
 * @param key the key signature
 * @param value the value signature
 * @return the associative array signature
 */
public static String createAssociativeArraySignature(String key, String value) {
	StringBuilder sb = new StringBuilder();
	sb.append(C_ASSOCIATIVE_ARRAY);
	sb.append(value);
	sb.append(key);
	return sb.toString();
}

/**
 * Creates an associative array signature with the given key and value
 * signatures.
 * @param key the key signature
 * @param value the value signature
 * @return the associative array signature
 */
public static char[] createAssociativeArraySignature(char[] key, char[] value) {
	char[] ret = new char[1 + key.length + value.length];
	ret[0] = C_ASSOCIATIVE_ARRAY;
	System.arraycopy(value, 0, ret, 1, value.length);
	System.arraycopy(key, 0, ret, 1 + value.length, key.length);
	return ret;
}

/**
 * Creates a typeof signature for the given expression.
 * @param expression an expression
 * @return the typeof signature
 */
public static String createTypeofSignature(String expression) {
	StringBuilder sb = new StringBuilder();
	sb.append(C_TYPEOF);
	sb.append(expression.length());
	sb.append(C_TYPEOF);
	sb.append(expression);
	return sb.toString();
}

/**
 * Creates a typeof signature for the given expression.
 * @param expression an expression
 * @return the typeof signature
 */
public static char[] createTypeofSignature(char[] expression) {
	int expressionLength = expression.length;
	int expressionLengthStringLength = lenghtOfLengthToString(expression);
	
	char[] ret = new char[2 + expressionLengthStringLength + expressionLength];
	ret[0] = C_TYPEOF;
	copyNumber(expressionLength, expressionLengthStringLength, ret, 1);
	ret[1 + expressionLengthStringLength] = C_TYPEOF;
	System.arraycopy(expression, 0, ret, 2 + expressionLengthStringLength, expressionLength);
	return ret;
}

/**
 * Creates a slice type signature for the given type, lower and upper bounds.
 * @param type the slice type
 * @param lower the lower bound
 * @param upper the upper bound
 * @return the slice signature
 */
public static String createSliceSignature(String type, String lower, String upper) {
	StringBuilder sb = new StringBuilder();
	sb.append(C_SLICE);
	sb.append(type);
	sb.append(lower.length());
	sb.append(C_SLICE);
	sb.append(lower);
	sb.append(upper.length());
	sb.append(C_SLICE);
	sb.append(upper);
	return sb.toString();
}

/**
 * Creates a slice type signature for the given type, lower and upper bounds.
 * @param type the slice type
 * @param lower the lower bound
 * @param upper the upper bound
 * @return the slice signature
 */
public static char[] createSliceSignature(char[] type, char[] lower, char[] upper) {
	int lowerLength = lower.length;
	int lowerLengthStringLength = lenghtOfLengthToString(lower);
	
	int upperLength = upper.length;
	int upperLengthStringLength = lenghtOfLengthToString(upper);
	
	char[] ret = new char[3 + type.length + lowerLengthStringLength + lowerLength + upperLengthStringLength + upperLength];
	ret[0] = C_SLICE;
	System.arraycopy(type, 0, ret, 1, type.length);
	
	copyNumber(lowerLength, lowerLengthStringLength, ret, 1 + type.length);
	ret[1 + lowerLengthStringLength + type.length] = C_SLICE;
	System.arraycopy(lower, 0, ret, 2 + lowerLengthStringLength + type.length, lowerLength);
	
	copyNumber(upperLength, upperLengthStringLength, ret, 2 + type.length + lowerLength + lowerLengthStringLength);
	ret[3 + type.length + lowerLength + lowerLengthStringLength] = C_SLICE;
	System.arraycopy(upper, 0, ret, 4 + type.length + lowerLength + lowerLengthStringLength, upperLength);
	
	return ret;
}

/**
 * Returns the number of parameter types in the given signature. The 
 * signature may include the full name qualification. If the signature is not
 * a function signature, zero is returned.
 *
 * @param signature a signature
 * @return the number of parameters
 * @exception IllegalArgumentException if the signature is not syntactically
 *   correct
 */
public static int getParameterCount(String signature) throws IllegalArgumentException {
	final int[] count = { 0 };	
	SignatureProcessor.process(signature, new SignatureRequestorAdapter() {
		
		private int functionTypeCount = 0;
		
		public void acceptArgumentModifier(int stc) { 
			if (functionTypeCount == 1) {
				count[0]++;
			}
		}

		public void enterFunctionType() {
			functionTypeCount++;
		}

		public void exitFunctionType(LINK link, String signature) {
			functionTypeCount--;
		}
		
	});
	return count[0];
}
/**
 * Returns the number of parameter types in the given signature. The 
 * signature may include the full name qualification. If the signature is not
 * a function signature, zero is returned.
 *
 * @param signature the signature
 * @return the number of parameters
 * @exception IllegalArgumentException if the signature is not syntactically
 *   correct
 */
public static int getParameterCount(char[] signature) throws IllegalArgumentException {
	if (signature == null) {
		throw new IllegalArgumentException();
	}
	return getParameterCount(new String(signature));
}

/**
 * Returns the number of template parameter types in the given template,
 * symbol template or function template signature. If the signature is not a template
 * signature, zero is returned.
 *
 * @param signature a signature
 * @return the number of template parameters
 * @exception IllegalArgumentException if the signature is not syntactically
 *   correct
 */
public static int getTemplateParameterCount(String signature) throws IllegalArgumentException {
	final int[] count = { 0 };	
	SignatureProcessor.process(signature, new SignatureRequestorAdapter() {
		private int templateCount;
		@Override
		public void enterTemplateAliasParameter() {
			if (templateCount == 1) {
				count[0]++;
			}
		}
		@Override
		public void enterTemplateTypeParameter() {
			if (templateCount == 1) {
				count[0]++;
			}
		}
		@Override
		public void enterTemplateValueParameter() {
			if (templateCount == 1) {
				count[0]++;
			}
		}
		@Override
		public void acceptTemplateTupleParameter() {
			if (templateCount == 1) {
				count[0]++;
			}
		}
		@Override
		public void enterTemplateParameters() {
			templateCount++;
			count[0] = 0;
		}	
		@Override
		public void exitTemplateParameters() {
			templateCount--;
		}
	});
	return count[0];
}

/**
 * Returns the number of template parameter types in the given template,
 * symbol template or function template signature. If the signature is not a template
 * signature, zero is returned.
 *
 * @param signature a signature
 * @return the number of template parameters
 * @exception IllegalArgumentException if the signature is not syntactically
 *   correct
 * @since 2.0
 */
public static int getTemplateParameterCount(char[] signature) throws IllegalArgumentException {
	return getTemplateParameterCount(new String(signature));
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
	final List<String> parameters = new ArrayList<String>();
	final boolean[] valid = { false };
	
	SignatureProcessor.process(methodSignature, new SignatureRequestorAdapter() {
		int functionCount = 0;
		int argumentsCount = 0;
		int templateInstanceCount = 0;
		boolean foundArgumentBreak = false;
		@Override
		public void acceptArgumentModifier(int stc) {
			if (functionCount == 1 && templateInstanceCount == 0 && !foundArgumentBreak) {
				argumentsCount++;
			}
		}
		@Override
		public void acceptPrimitive(TypeBasic type) {
			add(type.deco);
		}
		@Override
		public void acceptPointer(String signature) {
			replace(signature);
		}
		@Override
		public void acceptStaticArray(char[] dimension, String signature) {
			replace(signature);
		}
		@Override
		public void acceptDynamicArray(String signature) {
			replace(signature);
		}
		@Override
		public void acceptAssociativeArray(String signature) {
			parameters.remove(parameters.size() - 1);
			replace(signature);
		}
		@Override
		public void acceptTypeof(char[] expression, String signature) {
			add(signature);
		}
		@Override
		public void acceptTypeSlice(char[] lwr, char[] upr, String signature) {
			replace(signature);
		}
		@Override
		public void acceptArgumentBreak(char c) {
			if (functionCount == 1) {
				foundArgumentBreak = true;
			}
		}
		@Override
		public void acceptDelegate(String signature) {
			replace(signature);
		}
		@Override
		public void acceptIdentifier(char[][] compoundName, String signature) {
			add(signature);
		}
		@Override
		public void acceptSymbol(char type, char[] name, int startPosition, String signature) {
			if (parameters.size() == argumentsCount - 1) {
				add(signature);
			} else {
				replace(signature);
			}
		}
		@Override
		public void enterTemplateInstance() {
			templateInstanceCount++;
		}
		@Override
		public void exitTemplateInstance(String signature) {
			templateInstanceCount--;
			replace(signature);
		}
		@Override
		public void enterFunctionType() {
			functionCount++;
			valid[0] = true;
		}
		@Override
		public void exitFunctionType(LINK link, String signature) {
			functionCount--;
			add(signature);
		}
		private void add(String sig) {
			if (functionCount == 1 && templateInstanceCount == 0 && !foundArgumentBreak) {
				parameters.add(sig);
			}
		}
		private void replace(String sig) {
			if (functionCount == 1 && templateInstanceCount == 0 && !foundArgumentBreak) {
				parameters.set(parameters.size() - 1, sig);
			}
		}
	});
	
	if (!valid[0]) {
		throw new IllegalArgumentException();
	}
	
	return parameters.toArray(new String[parameters.size()]);
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
	final String[] ret = { null };
	final boolean[] valid = { false };
	
	SignatureProcessor.process(methodSignature, new SignatureRequestorAdapter() {
		int functionCount = 0;
		int templateInstanceCount = 0;
		@Override
		public void enterFunctionType() {
			functionCount++;
		}
		@Override
		public void exitFunctionType(LINK link, String signature) {
			functionCount--;
			if (functionCount != 0) {
				copy(signature);
			} else {
				valid[0] = true;
			}
		}
		@Override
		public void acceptPrimitive(TypeBasic type) {
			copy(type.deco);
		}
		@Override
		public void acceptAssociativeArray(String signature) {
			copy(signature);
		}
		@Override
		public void acceptDelegate(String signature) {
			copy(signature);
		}
		@Override
		public void acceptDynamicArray(String signature) {
			copy(signature);
		}
		@Override
		public void acceptStaticArray(char[] dimension, String signature) {
			copy(signature);
		}
		@Override
		public void acceptModule(char[][] compoundName, String signature) {
			copy(signature);
		}
		@Override
		public void acceptPointer(String signature) {
			copy(signature);
		}
		@Override
		public void acceptIdentifier(char[][] compoundName, String signature) {
			copy(signature);
		}
		@Override
		public void acceptSymbol(char type, char[] name, int startPosition, String signature) {
			copy(signature);
		}
		@Override
		public void acceptTypeof(char[] expression, String signature) {
			copy(signature);
		}
		@Override
		public void acceptTypeSlice(char[] lwr, char[] upr, String signature) {
			copy(signature);
		}
		@Override
		public void enterTemplateInstance() {
			templateInstanceCount++;
		}
		@Override
		public void exitTemplateInstance(String signature) {
			templateInstanceCount--;
			copy(signature);
		}
		private void copy(String signature) {
			if (functionCount == 1 && templateInstanceCount == 0) {
				ret[0] = signature;
			}
		}
	});
	
	if (!valid[0]) {
		throw new IllegalArgumentException();
	}
	return ret[0];
}

/**
 * Converts the given signature to a readable string.
 * 
 * <p>
 * For example:
 * <pre>
 * toCharArray({'@', '3', 'f', 'o', 'o', '4', 't', 'e', 's', 't', 'C', '3', 'B', 'a', 'r'}) -> {'f', 'o', 'o', '.', 't', 'e', 's', 't', '.', 'B', 'a', 'r'}
 * toCharArray({'i'}) -> {'i', 'n', 't'}
 * toCharArray({'P', 'i'}) -> {'i', 'n', 't', '*'}
 * toCharArray({'@', '3', 'f', 'o', 'o', '4', 't', 'e', 's', 't', '{', '3', 'B', 'a', 'r', '"'}) -> {'f', 'o', 'o', '.', 't', 'e', 's', 't', '.', 'B', 'a', 'r', '!', '(', ')'}
 * toCharArray({'@', '3', 'f', 'o', 'o', '4', 't', 'e', 's', 't', '{', '3', 'B', 'a', 'r', ']', ']', '"'}) -> {'f', 'o', 'o', '.', 't', 'e', 's', 't', '.', 'B', 'a', 'r', '!', '(', 'T', ',', ' ', 'U', ')'}
 * toCharArray({'F', 'i', 'Z', 'a'}) -> {'c', 'h', 'a', 'r', ' ', 'f', 'u', 'n', 'c', 't', 'i', 'o', 'n', '(', 'i', 'n', 't', ')'}
 * toCharArray({'D', 'F', 'i', 'Z', 'a'}) -> {'c', 'h', 'a', 'r', ' ', 'd', 'e', 'l', 'e', 'g', 'a', 't', 'e', '(', 'i', 'n', 't', ')'}"
 * </pre>
 * </p> 
 *
 * @param signature the type signature
 * @return the string representation of the type
 * @exception IllegalArgumentException if the signature is not syntactically
 *   correct
 */
public static char[] toCharArray(char[] signature) throws IllegalArgumentException {
	return toString(new String(signature)).toCharArray();
}
/**
 * Converts the given signature to a readable string.
 * 
 * <p>
 * For example:
 * <pre>
 * toString("@3foo4testC3Bar") -> "foo.test.Bar"
 * toString("i") -> "int"
 * toString("Pi") -> "int*"
 * toString("@3foo4test{3Bar\"") -> "foo.test.Bar!()"
 * toString("@3foo4test{3Bar]]\"") -> "foo.test.Bar!(T, U)"
 * toString("FiZa") -> "char function(int)"
 * toString("DFiZa") -> "char delegate(int)"
 * </pre>
 * </p>
 *
 * @param signature the type signature
 * @return the string representation of the type
 * @exception IllegalArgumentException if the signature is not syntactically
 *   correct
 */
public static String toString(String signature) throws IllegalArgumentException {
	final Stack<Stack<StringBuilder>> stack = new Stack<Stack<StringBuilder>>();
	stack.push(new Stack<StringBuilder>());
	
	final Stack<Stack<char[]>> modifiers = new Stack<Stack<char[]>>();
	final Stack<Stack<StringBuilder>> templateParameters = new Stack<Stack<StringBuilder>>();
	final Stack<Stack<StringBuilder>> templateInstances = new Stack<Stack<StringBuilder>>();
	
	SignatureProcessor.process(signature, new SignatureRequestorAdapter() {
		@Override
		public void acceptPrimitive(TypeBasic type) {
			Stack<StringBuilder> st = stack.peek();
			
			StringBuilder sb = new StringBuilder();
			sb.append(type.ty.name);
			
			st.push(sb);
		}
		@Override
		public void acceptPointer(String signature) {
			Stack<StringBuilder> st = stack.peek();
			
			st.peek().append('*');
		}
		@Override
		public void acceptDynamicArray(String signature) {
			Stack<StringBuilder> st = stack.peek();
			
			StringBuilder sb = st.peek();
			sb.append('[');
			sb.append(']');
		}
		@Override
		public void acceptStaticArray(char[] dimension, String signature) {
			Stack<StringBuilder> st = stack.peek();
			
			StringBuilder sb = st.peek();
			sb.append('[');
			sb.append(dimension);
			sb.append(']');
		}
		@Override
		public void acceptAssociativeArray(String signature) {
			Stack<StringBuilder> st = stack.peek();
			
			StringBuilder value = st.pop();
			StringBuilder key = st.pop();
			
			StringBuilder sb = new StringBuilder();
			sb.append(value);
			sb.append('[');
			sb.append(key);
			sb.append(']');
			
			st.push(sb);
		}
		@Override
		public void acceptTypeof(char[] expression, String signature) {
			Stack<StringBuilder> st = stack.peek();
			
			StringBuilder sb = new StringBuilder();
			sb.append("typeof(");
			sb.append(expression);
			sb.append(')');
			
			st.push(sb);
		}
		@Override
		public void acceptTypeSlice(char[] lwr, char[] upr, String signature) {
			Stack<StringBuilder> st = stack.peek();
			
			StringBuilder type = st.pop();
			
			StringBuilder sb = new StringBuilder();
			sb.append(type);
			sb.append('[');
			sb.append(lwr);
			sb.append(" .. ");
			sb.append(upr);
			sb.append(']');
			
			st.push(sb);
		}
		@Override
		public void acceptModule(char[][] compoundName, String signature) {
			acceptIdentifier(compoundName, signature);
		}
		@Override
		public void acceptSymbol(char type, char[] name, int startPosition, String signature) {
			if (type == ISignatureConstants.FUNCTION ||
				type == ISignatureConstants.TEMPLATED_FUNCTION) {
				Stack<StringBuilder> st = stack.peek();
				
				StringBuilder funcType = st.pop();
				StringBuilder funcName = st.pop();
				
				if (funcName.length() != 0) {
					funcName.append('.');
				}
				funcName.append(name);
				
				if (type == ISignatureConstants.TEMPLATED_FUNCTION) {
					appendTemplateParameters(funcName);
				}
				
				replaceFunctionWith(funcType, funcName.toString());
				
				st.push(funcType);
			} else {
				Stack<StringBuilder> st = stack.peek();
				
				StringBuilder sb = st.peek();
				if (sb.length() > 0) {
					sb.append('.');
				}
				sb.append(name);
				
				if (type == ISignatureConstants.TEMPLATE ||
					type == ISignatureConstants.TEMPLATED_CLASS ||
					type == ISignatureConstants.TEMPLATED_STRUCT ||
					type == ISignatureConstants.TEMPLATED_UNION ||
					type == ISignatureConstants.TEMPLATED_INTERFACE) {
					appendTemplateParameters(sb);
				}
			}
		}
		
		private void appendTemplateParameters(StringBuilder sb) {
			sb.append('!');
			sb.append('(');
			
			Stack<StringBuilder> tps = templateParameters.pop();				
			for (int i = 0; i < tps.size(); i++) {
				if (i != 0) {
					sb.append(',');
					sb.append(' ');
				}
				sb.append(tps.get(i));
			}
			
			sb.append(')');
		}
		
		@Override
		public void acceptIdentifier(char[][] compoundName, String signature) {
			Stack<StringBuilder> st = stack.peek();
			
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < compoundName.length; i++) {
				if (i != 0) {
					sb.append('.');
				}
				sb.append(compoundName[i]);
			}
			
			st.push(sb);
		}
		@Override
		public void enterFunctionType() {
			stack.push(new Stack<StringBuilder>());
			modifiers.push(new Stack<char[]>());
		}
		@Override
		public void acceptArgumentModifier(int stc) {
			switch(stc) {
			case STC.STCin: 
				modifiers.peek().push(CharOperation.NO_CHAR);
				break;
			case STC.STCout:
				modifiers.peek().push("out ".toCharArray());
				break;
			case STC.STCref:
				modifiers.peek().push("ref ".toCharArray());
				break;
			case STC.STClazy:
				modifiers.peek().push("lazy ".toCharArray());
				break;
			}
		}
		@Override
		public void exitFunctionType(LINK link, String signature) {
			Stack<StringBuilder> st = stack.pop();
			
			StringBuilder sb = new StringBuilder();
			sb.append(st.pop());
			sb.append(" function(");
			
			for (int i = 0; i < st.size(); i++) {
				if (i != 0) {
					sb.append(',');
					sb.append(' ');
				}
				sb.append(modifiers.peek().get(i));
				sb.append(st.get(i));
			}
			
			sb.append(')');
			
			stack.peek().push(sb);
			
			modifiers.pop();
		}
		@Override
		public void acceptDelegate(String signature) {
			Stack<StringBuilder> st = stack.peek();
			StringBuilder sb = st.peek();
			
			replaceFunctionWith(sb, "delegate");
		}
		private void replaceFunctionWith(StringBuilder func, String string) {
			int parenCount = 0;
			int funcIndex = -1;
			for(int i = 0; i < func.length(); i++) {
				char c = func.charAt(i);
				switch(c) {
				case '(':
					parenCount++;
					break;
				case ')':
					parenCount--;
					break;
				case 'f':
					if (parenCount == 0 && i + 8 < func.length() && func.substring(i, i + 8).equals("function")) {
						funcIndex = i;
					}
					break;
				}
			}
			
			func.replace(funcIndex, funcIndex + 8, string);
		}
		@Override
		public void enterTemplateParameters() {
			stack.push(new Stack<StringBuilder>());
			templateParameters.push(new Stack<StringBuilder>());
		}
		@Override
		public void acceptTemplateTupleParameter() {
			enterTemplateParameter();
			templateParameters.peek().peek().append("...");
		}
		@Override
		public void enterTemplateAliasParameter() {
			enterTemplateParameter();
			templateParameters.peek().peek().insert(0, "alias ");
		}
		@Override
		public void exitTemplateAliasParameter(String signature) {
			if (!stack.peek().isEmpty()) {
				templateParameters.peek().peek().append(' ').append(':').append(' ').append(stack.peek().peek());
			}
		}
		@Override
		public void enterTemplateTypeParameter() {
			enterTemplateParameter();
		}
		@Override
		public void exitTemplateTypeParameter(String signature) {
			if (!stack.peek().isEmpty()) {
				templateParameters.peek().peek().append(' ').append(':').append(' ').append(stack.peek().peek());
			}
		}
		@Override
		public void enterTemplateValueParameter() {
			enterTemplateParameter();
		}
		@Override
		public void exitTemplateValueParameter(String signature) {
			if (!stack.peek().isEmpty()) {
				templateParameters.peek().peek().insert(0, stack.peek().peek().append(' '));
			}
		}
		@Override
		public void acceptTemplateValueParameterSpecificValue(char[] exp) {
			templateParameters.peek().peek().append(' ').append(':').append(' ').append(exp);
		}
		private void enterTemplateParameter() {
			StringBuilder sb = new StringBuilder(1);
			sb.append(nextTemplateParameterName());
			
			templateParameters.peek().push(sb);
		}
		private char nextTemplateParameterName() {
			// We don't care about crazy templates with more than 8 parameters
			return (char) ('T' + templateParameters.peek().size());
		}
		@Override
		public void exitTemplateParameters() {
			stack.pop();
		}
		@Override
		public void enterTemplateInstance() {
			stack.push(new Stack<StringBuilder>());
			templateInstances.push(new Stack<StringBuilder>());			
		}
		@Override
		public void exitTemplateInstanceSymbol(String string) {
			templateInstances.peek().push(new StringBuilder(stack.peek().pop()));
		}
		@Override
		public void exitTemplateInstanceTypeParameter(String signature) {
			templateInstances.peek().push(new StringBuilder(stack.peek().pop()));
		}
		@Override
		public void acceptTemplateInstanceValue(char[] exp, String signature) {
			StringBuilder sb = new StringBuilder();
			sb.append(exp);
			
			templateInstances.peek().push(sb);
		}
		@Override
		public void exitTemplateInstance(String signature) {
			stack.pop();
			
			StringBuilder sb = stack.peek().peek();
			sb.append('!');
			sb.append('(');
			
			Stack<StringBuilder> tp = templateInstances.pop();
			for (int i = 0; i < tp.size(); i++) {
				if (i != 0) {
					sb.append(',');
					sb.append(' ');
				}
				sb.append(tp.get(i));
			}
			
			sb.append(')');
		}
	});
	
	return stack.pop().pop().toString();
}

/**
 * Determines if the given function type or delegate type signature is variadic.
 * @param signature a function type or delegate type signature
 * @return <code>true</code> if the signature is variadic, <code>false</code> otherwise
 * @throws IllegalArgumentException if the signature is malformed, or is not a function
 * type or delegate type signature
 */
public static boolean isVariadic(char[] signature) throws IllegalArgumentException {
	return isVariadic(new String(signature));
}

/**
 * Determines if the given function type or delegate type signature is variadic. The
 * signature may be fully qualified.
 * @param signature a function type or delegate type signature
 * @return <code>true</code> if the signature is variadic, <code>false</code> otherwise
 * @throws IllegalArgumentException if the signature is malformed
 */
public static boolean isVariadic(String signature) throws IllegalArgumentException {
	final boolean[] variadic = { false };
	final boolean[] valid = { false };
	
	SignatureProcessor.process(signature, new SignatureRequestorAdapter() {
		int functionCount = 0;
		@Override
		public void acceptArgumentBreak(char c) {
			if (functionCount == 1) {
				variadic[0] = 
					c == ISignatureConstants.FUNCTION_PARAMETERS_BREAK_VARIADIC ||
					c == ISignatureConstants.FUNCTION_PARAMETERS_BREAK_VARIADIC2;
			}
		}
		@Override
		public void acceptSymbol(char type, char[] name, int startPosition, String signature) {
			if (functionCount == 0) {
				valid[0] = 
					type == ISignatureConstants.FUNCTION ||
					type == ISignatureConstants.TEMPLATED_FUNCTION;
			}
		}
		@Override
		public void enterFunctionType() {
			functionCount++;
			valid[0] = true;
		}
		@Override
		public void exitFunctionType(LINK link, String signature) {
			functionCount--;
		}
	});
	
	if (!valid[0]) {
		throw new IllegalArgumentException();
	}
	
	return variadic[0];
}

/**
 * Returns the last segment of a fully qualified name. Segments
 * are divided by dots.
 * 
 * <p>
 * For example:
 * <pre>
 * getSimpleName("int") -> "int"
 * getSimpleName("foo.test.Bar") -> "Bar"
 * getSimpleName("foo.test.Bar!(int)") -> "Bar!(int)"
 * getSimpleName("foo.test.metdhod(int, float)") -> "method(int, float)"
 * </pre>
 * </p> * 
 * 
 * @param qualifiedName a fully qualified name
 * @return the last segment of the fully qualified name
 */
public static String getSimpleName(String qualifiedName) {
	int dot = 0;
	int parenCount = 0;
	for (int i = 0; i < qualifiedName.length(); i++) {
		char c = qualifiedName.charAt(i);
		switch(c) {
		case '(':
			parenCount++;
			break;
		case ')':
			parenCount--;
			break;
		case '.':
			if (parenCount == 0) {
				dot = i + 1;
			}
			break;
		}
	}
	if (dot == 0) {
		return qualifiedName;
	} else {
		return qualifiedName.substring(dot);
	}
}

/**
 * Returns the last segment of a fully qualified name. Segments
 * are divided by dots.
 * 
 * <p>
 * For example:
 * <pre>
 * getSimpleName("int") -> "int"
 * getSimpleName("foo.test.Bar") -> "Bar"
 * getSimpleName("foo.test.Bar!(int)") -> "Bar!(int)"
 * getSimpleName("foo.test.metdhod(int, float)") -> "method(int, float)"
 * </pre>
 * </p> * 
 * 
 * @param qualifiedName a fully qualified name
 * @return the last segment of the fully qualified name
 */
public static char[] getSimpleName(char[] qualifiedName) {
	int dot = 0;
	int parenCount = 0;
	for (int i = 0; i < qualifiedName.length; i++) {
		char c = qualifiedName[i];
		switch(c) {
		case '(':
			parenCount++;
			break;
		case ')':
			parenCount--;
			break;
		case '.':
			if (parenCount == 0) {
				dot = i + 1;
			}
			break;
		}
	}
	if (dot == 0) {
		return qualifiedName;
	} else {
		return CharOperation.subarray(qualifiedName, dot, qualifiedName.length);
	}
}

/*
 * Given a string, this method gets it's length, and, seen as a string,
 * returns it's length.
 */
private static int lenghtOfLengthToString(char[] string) {
	int length = string.length;
	int count = 0;
	while(length != 0) {
		length /= 10;
		count++;
	}
	return count;
}

/*
 * Copies a number into the given char array.
 * @param dimension the dimension to copy
 * @param dimensionStringLength the length of the dimension, seen as a string
 * @param ret where to copy
 * @param position where to start copying
 */
private static void copyNumber(int dimension, int dimensionStringLength, char[] ret, int position) {
	while(dimension != 0) {
		ret[position + dimensionStringLength - 1] = (char) ((dimension % 10) + '0');
		dimension /= 10;
		position--;
	}
}

}
