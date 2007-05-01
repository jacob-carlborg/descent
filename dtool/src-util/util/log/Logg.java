package util.log;


public class Logg {

	public static boolean masterLoggEnabled = true;

	public static Logg main = new Logg();
	public static Logg nolog = new Logg(false);
	public static Logg dtool = new Logg(); 
	public static Logg codeScanner = new Logg(false); 
	public static Logg builder = new Logg(); 
	public static Logg model = new Logg(); 

	public boolean enabled = true;
	
	public Logg(boolean enabled) {
		this.enabled = enabled;
	}
	public Logg() {
		this(true);
	}

	public void println(Object string) {
		print(string);
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
