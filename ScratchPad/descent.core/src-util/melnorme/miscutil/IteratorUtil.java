package melnorme.miscutil;

import java.util.Collections;
import java.util.Iterator;

public class IteratorUtil { 

	public static final Iterator<?> EMPTY_ITERATOR = Collections.EMPTY_LIST.iterator();
	
	@SuppressWarnings("unchecked")
	public static <T> Iterator<T> getEMPTY_ITERATOR() {
		return (Iterator<T>) EMPTY_ITERATOR;
	}

	@SuppressWarnings("unchecked")
	public static <T> Iterator<T> recast(Iterator<? extends T> iter) {
		return ((Iterator<T>) iter);
	}

	public static <T> Iterator<T> singletonIterator(T elem) {
		return Collections.singletonList(elem).iterator();
	}

}
