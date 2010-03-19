package scratch.utils;

import static melnorme.miscutil.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.ArrayUtil;
import melnorme.miscutil.IPredicate;

/**
 * Miscellaneous recently created utilities, which could be later refactored into other classes 
 */
public class NewUtils {
	
	public static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
	public static final DateFormat TIMESTAMPSHORT_FORMAT = new SimpleDateFormat("ss.SSS");

	
	public static <T> T[] array(T... elems) {
		return elems;
	}
	
	/** Creates a new array with given first element prepended to given rest array. */
	public static <T> T[] prepend(T first, T... rest) {
		T[] newArray = ArrayUtil.create(rest.length + 1, rest);
		newArray[0] = first;
		System.arraycopy(rest, 0, newArray, 1, rest.length);
		return newArray;
	}

	/** Create a new List with the same elements as given Collection. */
	public static <T> List<T> createCopy(Collection<T> coll) {
		return new ArrayList<T>(coll);
	}
	
	/** Removes from given list the first element that matches given predicate. */
	public static <T> void removeElement(List<? extends T> list, IPredicate<T> predicate) {
		for (Iterator<? extends T> iter = list.iterator(); iter.hasNext(); ) {
			T obj = iter.next();
			if(predicate.evaluate(obj)) {
				iter.remove();
			}
		}
	}
	
	public static <T extends Comparable<? super T>> List<T> sort(List<T> list) {
		Collections.sort(list);
		return list;
	}

	public static <T extends Comparable<? super T>> boolean arrayContainsSame(T[] arr1, T[] arr2) {
		List<T> list1 = Arrays.asList(arr1);
		List<T> list2 = Arrays.asList(arr2);
		return sort(list1).equals(sort(list2));
	}

	public static <T> boolean hasDuplicates(T[] filterOptions) {
		return hasDuplicates(Arrays.asList(filterOptions));
	}

	private static <T> boolean hasDuplicates(List<T> list) {
		return new HashSet<T>(list).size() != list.size();
	}
	
	public static <T> T[] copyFrom(T[] allowedValues) {
		return Arrays.copyOf(allowedValues, allowedValues.length);
	}
	
}
