package scratch;

import melnorme.miscutil.MiscUtil;

public class ClassLoadHolder {
	
	
	static {
		Foo.number = 666;
		System.out.println("Static");
		MiscUtil.sleepUnchecked(5000);
		Foo.number = 10;
	}
	
	protected static int number;

	
	public ClassLoadHolder() {
		System.out.println("Constructor");
	}
	
	
	{
		System.out.println("Foo");
	}
	
}
