package melnorme.miscutil;

public interface Function<T, R> {
	
	R evaluate(T obj);
	
}