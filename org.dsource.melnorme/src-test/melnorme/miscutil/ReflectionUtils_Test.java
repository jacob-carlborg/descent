package melnorme.miscutil;

import static melnorme.miscutil.Assert.assertEquals;

import org.junit.Test;


public class ReflectionUtils_Test extends ReflectionUtils {
	
	@SuppressWarnings("unused")
	static class Foo {
		private int a;
		static int sa;
	}
	
	@SuppressWarnings("unused")
	static class FooBar extends Foo {
		int b;
		private static int sb;
	}
	
	
	@Test
	public void test_ContainsSame() {
		FooBar foobar = new FooBar();
		foobar.b = 1;
		FooBar.sa = 20;
		
		assertEquals(readField(foobar, "a"), 0);
		assertEquals(readField(foobar, "b"), 1);
		assertEquals(readStaticField(FooBar.class, "sa"), 20);
		assertEquals(readStaticField(FooBar.class, "sb"), 0);

		writeField(foobar, "a", 3);
		assertEquals(readField(foobar, "a"), 3);
		writeField(foobar, "b", 4);
		assertEquals(readField(foobar, "b"), 4);
		writeStaticField(FooBar.class, "sa", 30);
		assertEquals(readStaticField(FooBar.class, "sa"), 30);
		writeStaticField(FooBar.class, "sb", 40);
		assertEquals(readStaticField(FooBar.class, "sb"), 40);
	}
	
	@Test(expected=NoSuchFieldException.class)
	public void test_Invalid() throws Exception {
		FooBar foobar = new FooBar();
		try {
			readField(foobar, "none");
		} catch (ExceptionAdapter e) {
			throw e.getCause();
		}
	}
}
