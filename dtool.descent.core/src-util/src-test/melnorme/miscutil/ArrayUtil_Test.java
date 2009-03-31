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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public final class ArrayUtil_Test {

	private static final Object OBJECT = new Object();
	private static final String STRING = "Foo";
	private static final List<InputStream> LIST_FOO = new ArrayList<InputStream>();
	private static final List<? extends InputStream> LIST_X_FOO = new ArrayList<FileInputStream>();
	private static final List<?> LIST_X_ = new ArrayList<Object>();
	@SuppressWarnings("unchecked")
	private static final List LIST = new ArrayList<Object>();

	
	@SuppressWarnings("unchecked")
	public static <T> List<T> list(T elems) {
		return Arrays.asList(elems);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Class<T> klass(T elem) {
		return (Class<T>) elem.getClass();
	}
	
	@Test
	public void test_createFrom() {
	
		ArrayUtil.createFrom(list(OBJECT), Object.class);
		
		ArrayUtil.createFrom(list(STRING), Object.class);
		ArrayUtil.createFrom(list(STRING), String.class);
		
		ArrayUtil.createFrom(list(LIST_FOO), Object.class);
		ArrayUtil.createFrom(list(LIST_FOO), List.class);
		ArrayUtil.createFrom(list(LIST_FOO), klass(LIST_FOO));
		ArrayUtil.createFrom(list(LIST_X_FOO), Object.class);
		ArrayUtil.createFrom(list(LIST_X_FOO), List.class);
		ArrayUtil.createFrom(list(LIST_X_), Object.class);
		ArrayUtil.createFrom(list(LIST_X_), List.class);
		ArrayUtil.createFrom(list(LIST), Object.class);
		ArrayUtil.createFrom(list(LIST), List.class);
		ArrayUtil.createFrom(list(LIST), klass(LIST));

		// The following must be tested manually
		// uncomment to make sure each line causes a compilation error

//		final List<Exception> LIST_EXCEPTION = new ArrayList<Exception>();
//		final List<Object> LIST_OBJ = new ArrayList<Object>();
//		// each line must cause "not applicable for the arguments":
//		ArrayUtil.createFrom(list(OBJECT), String.class); 
//		ArrayUtil.createFrom(list(LIST_FOO), String.class);
//		ArrayUtil.createFrom(list(LIST_FOO), klass(LIST_EXCEPTION));
//		ArrayUtil.createFrom(list(LIST_FOO), klass(LIST_OBJ));
//		ArrayUtil.createFrom(list(LIST_X_), klass(LIST_OBJ));
//		ArrayUtil.createFrom(list(LIST_X_), klass(LIST_X_));
//		ArrayUtil.createFrom(list(LIST_X_FOO), klass(LIST_X_FOO));

	}
	
	public final Integer[] EMPTY_INTEGER_ARRAY = array();

	public static <T> T[] array(T... elems) {
		return elems;
	}
    
	
    public void assertDeepEquals(Object[] a, Object[] b) {
    	assertTrue(Arrays.equals(a, b));
    }

	@Test
	public void test_filter() {
		Integer[] arr1 = array(0, 1, 2, 3, 4);
		
		assertDeepEquals(ArrayUtil.filter(arr1, new IPredicate<Integer>() {
			public boolean evaluate(Integer obj) { 
				return true; 
			};
		}), array(0, 1, 2, 3, 4));
		                                                            

		assertDeepEquals(ArrayUtil.filter(arr1, new IPredicate<Integer>() {
			public boolean evaluate(Integer obj) { 
				return obj.intValue() % 2 == 0; 
			};
		}), array(0, 2, 4));
		
		assertDeepEquals(ArrayUtil.filter(arr1, new IPredicate<Integer>() {
			public boolean evaluate(Integer obj) { 
				return false; 
			};
		}), EMPTY_INTEGER_ARRAY);
	}
	
}
