package melnorme.miscutil;

import static melnorme.miscutil.Assert.assertTrue;

import java.lang.reflect.Array;
import java.util.List;

public class ArrayUtil {
	
	/** Creates a new array with the given length, and of the same type
	 * as the given array. */
	@SuppressWarnings("unchecked")
	public static <T> T[] copyFrom(T[] array, int newLength) {
        T[] copy = (T[]) Array.newInstance(array.getClass().getComponentType(), newLength);
    	System.arraycopy(array, 0, copy, 0, Math.min(array.length, newLength));
    	return copy;
	}
	
	/** Creates a new array with the given length, and of type char[] */
	public static char[] copyFrom(char[] array, int newLength) {
        char[] copy = (char[]) Array.newInstance(array.getClass().getComponentType(), newLength);
    	System.arraycopy(array, 0, copy, 0, Math.min(array.length, newLength));
    	return copy;
	}
	
	/** Creates a new array with the given length, and of type char[] */
	public static byte[] copyFrom(byte[] array, int newLength) {
		byte[] copy = (byte[]) Array.newInstance(array.getClass().getComponentType(), newLength);
    	System.arraycopy(array, 0, copy, 0, Math.min(array.length, newLength));
    	return copy;
	}
	
	@Deprecated
	public static char[] createNew(char[] array, int length) {
		return copyFrom(array, length);
	}

	
    /**
     * Copies the specified range of the specified array into a new array.
     * The initial index of the range (<tt>from</tt>) must lie between zero
     * and <tt>original.length</tt>, inclusive.  The value at
     * <tt>original[from]</tt> is placed into the initial element of the copy
     * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
     * Values from subsequent elements in the original array are placed into
     * subsequent elements in the copy.  The final index of the range
     * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
     * may be greater than <tt>original.length</tt>, in which case
     * <tt>null</tt> is placed in all elements of the copy whose index is
     * greater than or equal to <tt>original.length - from</tt>.  The length
     * of the returned array will be <tt>to - from</tt>.
     * <p>
     * The resulting array is of exactly the same class as the original array.
     *
     * @param original the array from which a range is to be copied
     * @param from the initial index of the range to be copied, inclusive
     * @param to the final index of the range to be copied, exclusive.
     *     (This index may lie outside the array.)
     * @return a new array containing the specified range from the original array,
     *     truncated or padded with nulls to obtain the required length
     * @throws ArrayIndexOutOfBoundsException if <tt>from &lt; 0</tt>
     *     or <tt>from &gt; original.length()</tt>
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    @SuppressWarnings("unchecked")
	public static <T> T[] copyOfRange(T[] original, int from, int to) {
        return copyOfRange(original, from, to, (Class<T[]>) original.getClass());
    }
    
    /**
     * Copies the specified range of the specified array into a new array.
     * The initial index of the range (<tt>from</tt>) must lie between zero
     * and <tt>original.length</tt>, inclusive.  The value at
     * <tt>original[from]</tt> is placed into the initial element of the copy
     * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
     * Values from subsequent elements in the original array are placed into
     * subsequent elements in the copy.  The final index of the range
     * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
     * may be greater than <tt>original.length</tt>, in which case
     * <tt>null</tt> is placed in all elements of the copy whose index is
     * greater than or equal to <tt>original.length - from</tt>.  The length
     * of the returned array will be <tt>to - from</tt>.
     * The resulting array is of the class <tt>newType</tt>.
     *
     * @param original the array from which a range is to be copied
     * @param from the initial index of the range to be copied, inclusive
     * @param to the final index of the range to be copied, exclusive.
     *     (This index may lie outside the array.)
     * @param newType the class of the copy to be returned
     * @return a new array containing the specified range from the original array,
     *     truncated or padded with nulls to obtain the required length
     * @throws ArrayIndexOutOfBoundsException if <tt>from &lt; 0</tt>
     *     or <tt>from &gt; original.length()</tt>
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws NullPointerException if <tt>original</tt> is null
     * @throws ArrayStoreException if an element copied from
     *     <tt>original</tt> is not of a runtime type that can be stored in
     *     an array of class <tt>newType</tt>.
     * @since 1.6
     */
    @SuppressWarnings("unchecked")
	public static <T,U> T[] copyOfRange(U[] original, int from, int to, Class<? extends T[]> newType) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        T[] copy = ((Object)newType == (Object)Object[].class)
            ? (T[]) new Object[newLength]
            : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, from, copy, 0,
                         Math.min(original.length - from, newLength));
        return copy;
    }
    
    /** Create an array from the given list, with the given run-time 
     * component type.
     * If the list is null, a zero-length array is created. */
	@SuppressWarnings("unchecked")
	public static <T> T[] createFrom(List<T> list, Class<T> cpType) {
		if(list == null)
			return (T[])Array.newInstance(cpType, 0);
	
		return list.toArray((T[])Array.newInstance(cpType, list.size()));
	}

	/** Creates an array with the same size as the given list.
	 * If the list is null, a zero-length array is created. */
	@SuppressWarnings("unchecked")
	public static <T> T[] newSameSize(List<?> list, Class<T> cpType) {
		if(list == null)
			return (T[])Array.newInstance(cpType, 0);
		
		return (T[])Array.newInstance(cpType, list.size());
	}

	
	/** Appends an element to array, creating a new array. */
	public static <T> T[] append(T[] array, T element) {
		T[] newArray = copyFrom(array, array.length + 1);
		newArray[array.length] = element;
		return newArray;
	}

	/** Appends two arrays, creating a new array of the same runtime type as original. */
	public static <T> T[] concat(T[] original, T... second) {
		T[] newArray = copyFrom(original, original.length + second.length);
    	System.arraycopy(second, 0, newArray, original.length, second.length);
		return newArray;
	}
	
	/** Appends two arrays, creating a new array of given runtime type. */
	@SuppressWarnings("unchecked")
	public static <T> T[] concat(T[] original, T[] second, Class<?> arClass) {
		int newSize = original.length + second.length;
		T[] newArray = (T[]) Array.newInstance(arClass, newSize);
		System.arraycopy(original, 0, newArray, 0, original.length);
		System.arraycopy(second, 0, newArray, original.length, second.length);
		return newArray;
	}
	
	
	/** Removes the given array the first element that equals given obj. */
	public static<T> T[] remove(T[] array, T obj) {
		for (int i = 0; i < array.length; i++) {
			T elem = array[i];
			if(elem.equals(obj));
				return removeAt(array, i);
		}
		return array;
	}
	
	
	/** Removes the element at index ix from array, creating a new array. */
	public static <T> T[] removeAt(T[] array, int ix) {
		T[] newArray = copyFrom(array, array.length - 1);
		System.arraycopy(array, 0, newArray, 0, ix);
		System.arraycopy(array, ix + 1, newArray, ix, array.length - ix - 1);	
		return newArray;
	}
	

	
	/** Return the index of the first occurrence of elem in array, or -1 if no occurrences. */
	public static <T> int indexOf(T[] array, T elem) {
		for (int i = 0; i < array.length; i++) {
			if(array[i].equals(elem))
				return i;
		}
		return -1;
	}
	
	/** Return true if array contains an element equal to obj. */
	public static <T> boolean contains(T[] array, T obj) {
		for(T elem: array) {
			if(elem.equals(obj))
				return true;
		}
		return false;
	}
	
	/** Return the index of the first occurrence of elem in array, or -1 if no occurrences. */
	public static int indexOf(byte[] array, byte elem) {
		for (int i = 0; i < array.length; i++) {
			if(array[i] == elem)
				return i;
		}
		return -1;
	}


	
	/** Finds the index in the given array of the element that
	 * equal given elem. */
	public static <T> int getIndexOfEquals(T[] arr, T elem) {
		for (int i = 0; i < arr.length; i++) {
			if(arr[i].equals(elem));
				return i;
		}
		return -1;
	}

}
