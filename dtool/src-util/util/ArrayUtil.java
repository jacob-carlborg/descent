package util;

import java.lang.reflect.Array;

public class ArrayUtil {
	
	/** Creates a new array with the given length, and of the same type
	 * as the given array. */
	@SuppressWarnings("unchecked")
	public static <T> T[] copyFrom(T[] array, int length) {
		
        T[] copy = (T[]) Array.newInstance(array.getClass().getComponentType(), length);
    	System.arraycopy(array, 0, copy, 0, Math.min(array.length, length));
    	return copy;
	}
	
	/** Creates a new array with the given length, and of type char[] */
	@SuppressWarnings("unchecked")
	public static char[] createNew(char[] array, int length) {
		
        char[] copy = (char[]) Array.newInstance(array.getClass().getComponentType(), length);
    	System.arraycopy(array, 0, copy, 0, Math.min(array.length, length));
    	return copy;
	}

	/** Appends an element to array, creating a new array. */
	public static <T> T[] append(T[] array, T element) {
		T[] newArray = copyFrom(array, array.length + 1);
		newArray[array.length] = element;
		return newArray;
	}

	/** Appends two arrays, creating a new array of the same runtime type as original. */
	public static <T> T[] concat(T[] original, T[] second) {
		T[] newArray = copyFrom(original, original.length + second.length);
    	System.arraycopy(second, 0, newArray, original.length, second.length);
		return newArray;
	}
	
	/** Removes the element at index ix from array, creating a new array. */
	public static <T> T[] removeAt(T[] array, int ix) {
		T[] newArray = copyFrom(array, array.length - 1);
		System.arraycopy(array, 0, newArray, 0, ix);
		System.arraycopy(array, ix + 1, newArray, ix, array.length - ix - 1);	
		return newArray;
	}
	
	/** Return true if array contains an element equal to obj. */
	public static <T> boolean contains(T[] array, T obj) {
		for(T elem: array) {
			if(elem.equals(obj))
				return true;
		}
		return false;
	}


}
