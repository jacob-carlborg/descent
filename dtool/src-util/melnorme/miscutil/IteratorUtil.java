package melnorme.miscutil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import dtool.dom.ast.ASTNode;
import dtool.dom.declarations.ImportAliasing.ImportAliasingDefUnit;

public class IteratorUtil {

	public static final Iterator EMPTY_ITERATOR = Collections.EMPTY_LIST.iterator();
	
	@SuppressWarnings("unchecked")
	public static <T> Iterator<T> getEMPTY_ITERATOR() {
		return EMPTY_ITERATOR;
	}

	@SuppressWarnings("unchecked")
	public static <T> Iterator<T> recast(Iterator<? extends T> iter) {
		return ((Iterator<T>) iter);
	}

	public static <T> Iterator<T> singletonIterator(T elem) {
		return Collections.singletonList(elem).iterator();
	}

}
