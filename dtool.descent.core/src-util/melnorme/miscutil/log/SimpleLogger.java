package melnorme.miscutil.log;


public class SimpleLogger {

	public static boolean masterLoggEnabled = true;

	protected boolean enabled = true;
	
	public SimpleLogger(boolean enabled) {
		this.enabled = enabled;
	}
	public SimpleLogger() {
		this(true);
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
	}

	public void println(Object... objs) {
		for(Object obj : objs)
			print(obj);
		println();
	}

	public void print(Object string) {
		if (masterLoggEnabled && enabled)
			System.out.print(string);
	}

	public void println() {
		if (masterLoggEnabled && enabled)
			System.out.println();
	}

}
