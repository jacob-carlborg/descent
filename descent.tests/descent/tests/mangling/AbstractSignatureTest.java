package descent.tests.mangling;

import junit.framework.TestCase;
import descent.core.Signature;

public class AbstractSignatureTest extends TestCase implements ISignatureTest {

	protected String A(String type) {
		return String.valueOf(Signature.C_DYNAMIC_ARRAY) + type;
	}
	
	protected String P(String type) {
		return String.valueOf(Signature.C_POINTER) + type;
	}
	
	protected String G(String type, String dim) {
		return String.valueOf(Signature.C_STATIC_ARRAY + type + Signature.C_STATIC_ARRAY2 + string(dim, Signature.C_STATIC_ARRAY));
	}
	
	protected String H(String key, String value) {
		return String.valueOf(String.valueOf(Signature.C_ASSOCIATIVE_ARRAY) + key + value);
	}
	
	protected String typeof(String exp) {
		return String.valueOf(String.valueOf(Signature.C_TYPEOF) + string(exp, Signature.C_TYPEOF));
	}
	
	protected String slice(String type, String lwr, String upr) {
		return String.valueOf(String.valueOf(Signature.C_SLICE) + type + Signature.C_SLICE2 + string(lwr, Signature.C_SLICE) + string(upr, Signature.C_SLICE));
	}
	
	private String string(String s, char separator) {
		return String.valueOf(s.length()) + separator + s;
	}

}
