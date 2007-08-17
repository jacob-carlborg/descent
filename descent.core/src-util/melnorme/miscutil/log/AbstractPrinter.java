package melnorme.miscutil.log;

public abstract class AbstractPrinter implements IPrinter {

	public abstract void print(String str);
	
	public abstract void println();

	public void println(String str) {
		print(str);
		println();
	}
	
	public void println(Object obj) {
		println(obj.toString());
	}
	
}
