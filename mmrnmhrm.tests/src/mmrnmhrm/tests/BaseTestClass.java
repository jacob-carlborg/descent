package mmrnmhrm.tests;


import junit.framework.Assert;



public class BaseTestClass {

	protected static void assertTrue(boolean b) {
		Assert.assertTrue(b);
	}
	
	protected static void assertTrue(boolean b, String msg) {
		Assert.assertTrue(msg, b);
	}
	
	protected static void assertTruePrintln(boolean b, String msg) {
		if(b == false)
			System.out.println(msg);
		Assert.assertTrue(msg, b);
	}
}