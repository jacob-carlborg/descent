package descent.core;

import java.math.BigDecimal;
import java.math.BigInteger;

import descent.core.dom.CompilationUnit;


/**
 * Represents the result of a compile-time evaluation.
 * 
 * <p><i>This interface is not intended to be implemented by clients.</i></p>
 * 
 * @see ICodeAssist#codeEvaluate(int)
 * @see ICodeAssist#codeEvaluate(int)
 */
public interface IEvaluationResult {
	
	/**
	 * A void result.
	 * {@link #getValue()} will be <code>null</code>.
	 */
	int VOID = 0;
	
	/**
	 * A boolean result.
	 * {@link #getValue()} can be safely cast to {@link Boolean}.
	 */
	int BOOL = 1;
	
	/**
	 * A byte result.
	 * {@link #getValue()} can be safely cast to {@link Byte}.
	 */
	int BYTE = BOOL + 1;
	
	/**
	 * An unsigned byte result.
	 * {@link #getValue()} can be safely cast to {@link Short}.
	 */
	int UBYTE = BYTE + 1;
	
	/**
	 * A short result.
	 * {@link #getValue()} can be safely cast to {@link Short}.
	 */
	int SHORT = UBYTE + 1;
	
	/**
	 * An unsigned short result.
	 * {@link #getValue()} can be safely cast to {@link Integer}.
	 */
	int USHORT = SHORT + 1;
	
	/**
	 * An int result.
	 * {@link #getValue()} can be safely cast to {@link Integer}.
	 */
	int INT = USHORT + 1;
	
	/**
	 * An unsigned int result.
	 * {@link #getValue()} can be safely cast to {@link Long}.
	 */
	int UINT = INT + 1;
	
	/**
	 * A long result.
	 * {@link #getValue()} can be safely cast to {@link Long}.
	 */
	int LONG = UINT + 1;
	
	/**
	 * An unsigned long result.
	 * {@link #getValue()} can be safely cast to {@link BigInteger}.
	 */
	int ULONG = LONG + 1;
	
	/**
	 * A char result.
	 * {@link #getValue()} can be safely cast to {@link Character}.
	 */
	int CHAR = ULONG + 1;
	
	/**
	 * A wchar result.
	 * {@link #getValue()} can be safely cast to {@link Character}.
	 */
	int WCHAR = CHAR + 1;
	
	/**
	 * A dchar result.
	 * {@link #getValue()} can be safely cast to {@link Character}.
	 */
	int DCHAR = WCHAR + 1;
	
	/**
	 * A float result.
	 * {@link #getValue()} can be safely cast to {@link Float}.
	 */
	int FLOAT = DCHAR + 1;
	
	/**
	 * A float result.
	 * {@link #getValue()} can be safely cast to {@link Double}.
	 */
	int DOUBLE = FLOAT + 1;
	
	/**
	 * A real result.
	 * {@link #getValue()} can be safely cast to {@link BigDecimal}.
	 */
	int REAL = DOUBLE + 1;
	
	/**
	 * A char[] result.
	 * {@link #getValue()} can be safely cast to {@link String}.
	 */
	int CHAR_ARRAY = REAL + 1;
	
	/**
	 * A wchar[] result.
	 * {@link #getValue()} can be safely cast to {@link String}.
	 */
	int WCHAR_ARRAY = CHAR_ARRAY + 1;
	
	/**
	 * A dchar[] result.
	 * {@link #getValue()} can be safely cast to {@link String}.
	 */
	int DCHAR_ARRAY = WCHAR_ARRAY + 1;
	
	/**
	 * An ifloat result.
	 * {@link #getValue()} can be safely cast to {@link Complex}.
	 */
	int IFLOAT = DCHAR_ARRAY + 1;
	
	/**
	 * An idouble result.
	 * {@link #getValue()} can be safely cast to {@link Complex}.
	 */
	int IDOUBLE = IFLOAT + 1;
	
	/**
	 * An ireal result.
	 * {@link #getValue()} can be safely cast to {@link Complex}.
	 */
	int IREAL = IDOUBLE + 1;
	
	/**
	 * An cfloat result.
	 * {@link #getValue()} can be safely cast to {@link Complex}.
	 */
	int CFLOAT = IREAL + 1;
	
	/**
	 * An cdouble result.
	 * {@link #getValue()} can be safely cast to {@link Complex}.
	 */
	int CDOUBLE = CFLOAT + 1;
	
	/**
	 * An creal result.
	 * {@link #getValue()} can be safely cast to {@link Complex}.
	 */
	int CREAL = CDOUBLE + 1;
	
	/**
	 * An array result (which is not char[], dchar[] or wchar[]).
	 * {@link #getValue()} can be safely cast to {@link IEvaluationResult[]}.
	 */
	int ARRAY = CREAL + 1;
	
	/**
	 * A compilation unit result.
	 * {@link #getValue()} can be safely cast to {@link CompilationUnit}.
	 */
	int COMPILATION_UNIT = ARRAY + 1;
	
	/**
	 * A struct literal result.
	 * {@link #getValue()} can be safely cast to IStructLiteral.
	 */
	int STRUCT_LITERAL = COMPILATION_UNIT + 1;
	
	/**
	 * A tuple result.
	 * {@link #getValue()} can be safely cast to {@link IEvaluationResult[]}.
	 */
	int TUPLE = STRUCT_LITERAL + 1;
	
	/**
	 * Returns the kind of this evaluation result. This is one of this
	 * interface constants.
	 * @return the kind of this evaluation result
	 */
	int getKind();
	
	/**
	 * Returns the value yield by the evaluation result. The class of
	 * the returned object can be determined with {@link #getKind()}.
	 * @return the value yield by the evaluation result
	 */
	Object getValue();

}
