package mmrnmhrm.tests;


import junit.framework.Assert;



public class BaseTest {

	protected static void assertTrue(boolean b) {
		Assert.assertTrue("Assertion failed.", b);
	}
	
	protected static void assertTrue(boolean b, String msg) {
		Assert.assertTrue(msg, b);
	}
	
	protected static void assertTrueP(boolean b, String msg) {
		if(b == false)
			System.out.println(msg);
		Assert.assertTrue(msg, b);
	}
}