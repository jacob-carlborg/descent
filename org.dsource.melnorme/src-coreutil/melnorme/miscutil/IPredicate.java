package melnorme.miscutil;

/**
 * A delegate method that given an argument of type T returns true or false 
 */
public interface IPredicate<T> {

	boolean evaluate(T obj);
	
}
