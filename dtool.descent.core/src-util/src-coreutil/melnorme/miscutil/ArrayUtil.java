/*******************************************************************************
 * Copyright (c) 2007 DSource.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial implementation
 *******************************************************************************/
package melnorme.miscutil;

import static melnorme.miscutil.Assert.assertTrue;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

public class ArrayUtil {
	
	/** Creates a new array of given length, and same component type as given compType. */
	@SuppressWarnings("unchecked")
	public static <T> T[] create(int length, T[] compType) {
		return (T[]) Array.newInstance(compType.getClass().getComponentType(), length);
	}
	
	/** Creates a new array with the given length, and of the same type as the given array. */
	@SuppressWarnings("unchecked")
	public static <T> T[] copyFrom(T[] array, int newLength) {
        T[] copy = (T[]) Array.newInstance(array.getClass().getComponentType(), newLength);
    	System.arraycopy(array, 0, copy, 0, Math.min(array.length, newLength));
    	return copy;
	}
	
	/** Creates a copy of given array with the given newlength, and of type char[]. */
	public static char[] copyFrom(char[] array, int newLength) {
        char[] copy = (char[]) Array.newInstance(Character.TYPE, newLength);
    	System.arraycopy(array, 0, copy, 0, Math.min(array.length, newLength));
    	return copy;
	}
	
	/** Creates a copy of given array with the given newlength, and of type byte[]. */
	public static byte[] copyFrom(byte[] array, int newLength) {
		byte[] copy = (byte[]) Array.newInstance(Byte.TYPE, newLength);
    	System.arraycopy(array, 0, copy, 0, Math.min(array.length, newLength));
    	return copy;
	}
	
	/** Creates a copy of given array with the given newlength, and of type int[]. */
	public static int[] copyFrom(int[] array, int newLength) {
		int[] copy = (int[]) Array.newInstance(Integer.TYPE, newLength);
    	System.arraycopy(array, 0, copy, 0, Math.min(array.length, newLength));
    	return copy;
	}
	
	/** Creates a copy of given array, and of type int[]. */
	public static int[] copyFrom(int[] array) {
		return copyFrom(array, array.length);
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
    
    /** Create an array from the given list, with the given run-time component type.
     * If the list is null, a zero-length array is created. */
	@SuppressWarnings("unchecked")
	public static <T> T[] createFrom(Collection<? extends T> list, Class<T> cpType) {
		if(list == null)
			return (T[]) Array.newInstance(cpType, 0);
		else
			return list.toArray((T[])Array.newInstance(cpType, list.size()));
	}
	
	/** Creates an int[] from given coll of Integers. */
	public static int[] createIntArray(List<? extends Integer> coll) {
		int[] array = new int[coll.size()];
		for (int i = 0; i < coll.size(); i++) {
			array[i] = coll.get(i);
		}
		return array;
	}
	

	/** Creates an array with the same size as the given list.
	 * If the list is null, a zero-length array is created. */
	@SuppressWarnings("unchecked")
	public static <T> T[] newSameSize(List<?> list, Class<T> cpType) {
		if(list == null)
			return (T[]) Array.newInstance(cpType, 0);
		else 
			return (T[]) Array.newInstance(cpType, list.size());
	}

    /** Copies src array range [0 .. src.length] to dest array starting at destIx. */
	public static void copyToRange(byte[] src, byte[] dest, int destIx) {
		assertTrue(src.length < dest.length - destIx);
		System.arraycopy(src, 0, dest, destIx, src.length);
	}
	
	
	/** Appends an element to array, creating a new array. */
	public static <T> T[] append(T[] base, T element) {
		T[] newArray = copyFrom(base, base.length + 1);
		newArray[base.length] = element;
		return newArray;
	}

	/** Appends given array other to given array base, 
	 * creating a new array of the same runtime type as original. */
	public static <T> T[] concat(T[] base, T... other) {
		return concat(base, other, other.length);
	}
	
	/** Appends appendCount number of elements of given array other to given array base, 
	 * creating a new array of the same runtime type as original. */
	public static <T> T[] concat(T[] base, T[] other, int appendCount) {
		T[] newArray = copyFrom(base, base.length + appendCount);
		System.arraycopy(other, 0, newArray, base.length, appendCount);
		return newArray;
	}

	/** Appends appendCount number of elements of given array other to given array base */
	public static byte[] concat(byte[] base, byte[] other, int appendCount) {
		final int length = base.length;
		byte[] newArray = copyFrom(base, base.length + appendCount);
		System.arraycopy(other, 0, newArray, length, appendCount);
		return newArray;
	}
	
	/** Appends appendCount number of elements of given array other to given array base */
	public static char[] concat(char[] base, char[] other, int appendCount) {
		final int length = base.length;
		char[] newArray = copyFrom(base, base.length + appendCount);
		System.arraycopy(other, 0, newArray, length, appendCount);
		return newArray;
	}
	
	/** Appends two arrays, creating a new array of given runtime type. */
	@SuppressWarnings("unchecked")
	public static <T> T[] concat(T[] base, T[] other, Class<?> arClass) {
		int newSize = base.length + other.length;
		T[] newArray = (T[]) Array.newInstance(arClass, newSize);
		System.arraycopy(base, 0, newArray, 0, base.length);
		System.arraycopy(other, 0, newArray, base.length, other.length);
		return newArray;
	}
	
	
	/** Removes the given array the first element that equals given obj. */
	public static<T> T[] remove(T[] array, T obj) {
		for (int i = 0; i < array.length; i++) {
			T elem = array[i];
			if(elem.equals(obj))
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
	
	/** Returns the index in given array of the first occurrence of given elem, 
	 * or -1 if none is found. */
	public static int indexOf(byte[] array, byte elem) {
		for (int i = 0; i < array.length; i++) {
			if(array[i] == elem)
				return i;
		}
		return -1;
	}
	
	@Deprecated
	public static <T> int getIndexOfEquals(T[] array, T elem) {
		return indexOfUsingEquals(array, elem);
	}
	
	/** Returns the index in given array of the first element that equals given elem, 
	 * or -1 if none is found. */
	public static <T> int indexOfUsingEquals(T[] array, T elem) {
		for (int i = 0; i < array.length; i++) {
			if(array[i].equals(elem))
				return i;
		}
		return -1;
	}
	
	/** Return true if array contains an element that matched given predicate */
	public static <T> boolean search(T[] array, IPredicate<T> predicate) {
		for(T elem: array) {
			if(predicate.evaluate(elem))
				return true;
		}
		return false;
	}
	
	/** Filters given array, using given predicate, creating a new array. */
	public static <T> T[] filter(T[] array, IPredicate<T> predicate) {
		T[] newArray = create(array.length, array);
		assertTrue(newArray.length <= array.length);
		int newIx = 0, arrayIx = 0;
		while(arrayIx < array.length) {
			if(predicate.evaluate(array[arrayIx])) {
				newArray[newIx] = array[arrayIx];
				newIx++; arrayIx++;
			} else {
				arrayIx++;
			}
		}
		return newIx == arrayIx ? newArray : ArrayUtil.copyOfRange(newArray, 0, newIx);
	}
	
}
