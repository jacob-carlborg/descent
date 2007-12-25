package descent.internal.compiler.parser;

import java.math.BigInteger;

/**
 * Encodes constant expressions into strings, and viceversa.
 */
public class ExpressionEncoder {
	
	private final static char INTEGER_EXP = 'I';
	
	// TODO see how to encode integer_t for best performance
	
	public static String encode(Expression value) {
		if (value instanceof IntegerExp) {
			IntegerExp i = (IntegerExp) value;
			return INTEGER_EXP + i.value.toString();
		} else {
			return null;
		}
	}
	
	public static Expression decode(String value) {
		if (value == null || value.length() == 0) {
			return null;
		}
		
		char c = value.charAt(0);
		switch(c) {
		case INTEGER_EXP:
			return new IntegerExp(Loc.ZERO, new integer_t(new BigInteger(value.substring(1))));
		default:
			return null;
		}
	}

}
