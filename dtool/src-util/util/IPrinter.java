package util;

/**
 * Interface for an object that allows printing of String's. 
 */
public interface IPrinter {

	public void print(String str);

	public void println();

	public void println(String str);

	public void println(Object obj);

}