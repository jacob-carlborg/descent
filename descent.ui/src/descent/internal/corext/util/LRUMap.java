package descent.internal.corext.util;

import java.util.LinkedHashMap;

/**
 * 
 */
public class LRUMap extends LinkedHashMap {
	
	private static final long serialVersionUID= 1L;
	private final int fMaxSize;
	
	public LRUMap(int maxSize) {
		super(maxSize, 0.75f, true);
		fMaxSize= maxSize;
	}
	
	protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
		return size() > fMaxSize;
	}
}
