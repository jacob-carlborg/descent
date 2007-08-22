package descent.tests.biginteger;

import java.math.BigInteger;

import descent.internal.compiler.parser.BigIntegerUtils;
import descent.internal.compiler.parser.IntegerWrapper;

import junit.framework.TestCase;

public class Cast_Tests extends TestCase {
	
	public void testCastInt8() {
		for(int i = -1000; i < 1000; i++) {
			assertEquals(new BigInteger(String.valueOf((byte) i)), BigIntegerUtils.castToInt8(new IntegerWrapper(i)).bigIntegerValue());
		}		
	}
	
	public void testCastInt16() {
		for(int i = -100000; i < 100000; i+=100) {
			assertEquals(new BigInteger(String.valueOf((short) i)), BigIntegerUtils.castToInt16(new IntegerWrapper(i)).bigIntegerValue());
		}		
	}
	
	public void testCastInt32() {
		for(long i = -1000000000000000000l; i < 1000000000000000000l; i+=1000000000000000l) {
			assertEquals(new BigInteger(String.valueOf((int) i)), BigIntegerUtils.castToInt32(new IntegerWrapper(new BigInteger(String.valueOf(i)))).bigIntegerValue());
		}		
	}

}
