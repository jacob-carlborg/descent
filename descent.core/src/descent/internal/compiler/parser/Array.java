package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.Collection;

public class Array<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;
	
	public Array() {
	}
	
	public Array(int capacity) {
		super(capacity);
	}
	
	public Array(Collection<? extends T> elements) {
		super(elements);
	}
	
	public void setDim(int dim) {
		ensureCapacity(dim);
	}
	
	@Override
	public T set(int index, T element) {
		if (index < size()) {
			return super.set(index, element);
		} else if (index == size()) {
			add(element);
			return null;
		} else {
			for(int i = size(); i < index; i++) {
				add(i, null);
			}
			add(element);
			return null;
		}
	}
	
	public void zero() {
		for(int i = 0; i < size(); i++) {
			set(i, null);
		}
	}

}
