package testdependencies;

public class Foo_PlatformRestricted {
	
	public static final int TEST_CONSTANT = 42;
	public static final int TEST_CONSTANT2;
	
	static {
		TEST_CONSTANT2 = 42;
	}	
	
	public static boolean hasFunc() {
		return true;
	}
	
	public static String calcString() {
		return "Dep String " ;
	}
}
