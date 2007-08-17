package melnorme.miscutil;

import java.util.Iterator;

/**
 */
public class ChainedIterator<T> implements Iterator<T> {

    Iterator<? extends T> firstIter;
    Iterator<? extends T> secondIter;
    //Iterator<T> currentIter;
    
	public static <U> Iterator<? extends U> create(
			Iterator<? extends U> firstIter,
			Iterator<? extends U> secondIter) {
		return new ChainedIterator<U>(firstIter, secondIter);
	}
    
    public ChainedIterator(Iterator<? extends T> firstIter, Iterator<? extends T> secondIter) {
    	this.firstIter = firstIter;
    	this.secondIter = secondIter;
    	//currentIter = firstIter;
    }

    public boolean hasNext() {
    	return firstIter.hasNext() || secondIter.hasNext();

    }

    public T next() {
    	if(firstIter.hasNext())
    		return firstIter.next();
    	return secondIter.next();

    }
    public void remove() {
        throw new UnsupportedOperationException();
    }

}

