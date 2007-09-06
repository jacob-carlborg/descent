package descent.tests.mars;

import java.math.BigInteger;

import junit.framework.TestCase;
import descent.internal.compiler.parser.integer_t;

public class integer_t_Test extends TestCase {
	
	public void testCastInt8() {
		for(int i = -1000; i < 1000; i++) {
			assertEquals(new integer_t((byte) i), new integer_t(i).castToInt8().bigIntegerValue());
		}
	}
	
	public void testCastInt16() {
		for(int i = -100000; i < 100000; i+=100) {
			assertEquals(new integer_t((short) i), new integer_t(i).castToInt16().bigIntegerValue());
		}
	}
	
	public void testCastInt32() {
		for(long i = -1000000000000000000l; i < 1000000000000000000l; i+=1000000000000000l) {
			assertEquals(new integer_t((int) i), new integer_t(BigInteger.valueOf(i)).castToInt32().bigIntegerValue());
		}
	}

}
