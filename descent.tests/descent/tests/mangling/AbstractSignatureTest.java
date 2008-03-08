package descent.tests.mangling;

import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.TY;
import junit.framework.TestCase;

public class AbstractSignatureTest extends TestCase implements ISignatureConstants {
	
	protected String F = String.valueOf(LINK_D);	
	protected String Z = String.valueOf(FUNCTION_PARAMETERS_BREAK);
	protected String Y = String.valueOf(FUNCTION_PARAMETERS_BREAK_VARIADIC);
	protected String X = String.valueOf(FUNCTION_PARAMETERS_BREAK_VARIADIC2);
	protected String i = String.valueOf(TY.Tint32.mangleChar);
	protected String a = String.valueOf(TY.Tchar.mangleChar);
	protected String v = String.valueOf(TY.Tvoid.mangleChar);
	protected String D = String.valueOf(DELEGATE);

	protected String A(String type) {
		return String.valueOf(DYNAMIC_ARRAY) + type;
	}
	
	protected String P(String type) {
		return String.valueOf(POINTER) + type;
	}
	
	protected String G(String type, String dim) {
		return String.valueOf(STATIC_ARRAY + type + string(dim, STATIC_ARRAY));
	}
	
	protected String H(String key, String value) {
		return String.valueOf(String.valueOf(ASSOCIATIVE_ARRAY) + key + value);
	}
	
	protected String typeof(String exp) {
		return String.valueOf(String.valueOf(TYPEOF) + string(exp, TYPEOF));
	}
	
	protected String slice(String type, String lwr, String upr) {
		return String.valueOf(String.valueOf(SLICE) + type + string(lwr, SLICE) + string(upr, SLICE));
	}
	
	private String string(String s, char separator) {
		return String.valueOf(s.length()) + separator + s;
	}

}
