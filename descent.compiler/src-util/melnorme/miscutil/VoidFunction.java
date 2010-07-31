package melnorme.miscutil;


public interface VoidFunction<T> extends Function<T, Void> {
	
	@Override
	Void evaluate(T obj);
	
}