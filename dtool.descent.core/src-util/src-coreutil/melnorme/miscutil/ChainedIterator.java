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

    @Override
	public boolean hasNext() {
    	return firstIter.hasNext() || secondIter.hasNext();

    }

    @Override
	public T next() {
    	if(firstIter.hasNext())
    		return firstIter.next();
    	return secondIter.next();

    }
    @Override
	public void remove() {
        throw new UnsupportedOperationException();
    }

}

