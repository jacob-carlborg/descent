package util;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayUtil {

	/** Appends an element to array, creating a new array. */
	public static <T> T[] append(T[] array, T element) {
		T[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = element;
		return newArray;
	}

	/** Removes the element at index ix from array, creating a new array. */
	public static <T> T[] removeAt(T[] array, int ix) {
		T[] newArray = createNew(array, array.length - 1);
		System.arraycopy(array, 0, newArray, 0, ix);
		System.arraycopy(array, ix + 1, newArray, ix, array.length - ix - 1);	
		return newArray;
	}
	
	/** Creates a new array with the given length, and of the same type
	 * as the given array. */
	@SuppressWarnings("unchecked")
	public static <T> T[] createNew(T[] array, int length) {
		
        T[] copy = (T[]) Array.newInstance(array.getClass().getComponentType(), length);
    	System.arraycopy(array, 0, copy, 0, Math.min(array.length, length));
    	return copy;
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
