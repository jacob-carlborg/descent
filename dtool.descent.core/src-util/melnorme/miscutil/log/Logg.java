package melnorme.miscutil.log;


public class Logg {

	public static boolean masterLoggEnabled = true;

	public static Logg nolog = new Logg(false);
	public static Logg main = new Logg();
	public static Logg model = new Logg(); 
	public static Logg codeScanner = new Logg(false); 
	public static Logg builder = new Logg(true); 

	protected boolean enabled = true;
	
	public Logg(boolean enabled) {
		this.enabled = enabled;
	}
	public Logg() {
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
