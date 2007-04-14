package util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

	/** Creates a List copy of orig, with all elements except elements equal
	 * to outElem. */
	public static <T> List<T> copyExcept(T[] orig, T outElem) {
		List<T> rejectedElements = 
			new ArrayList<T>(orig.length);

		for (int i= 0; i < orig.length; i++) {
			if (!orig[i].equals(outElem)) {
				rejectedElements.add(orig[i]);
			}
		}
		return rejectedElements;
	}

}
