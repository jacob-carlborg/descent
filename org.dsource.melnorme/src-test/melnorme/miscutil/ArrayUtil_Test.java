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
	@SuppressWarnings("rawtypes")
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
	@SuppressWarnings("unused")
	public void test_createFrom() {
	
		Object[]  obj01 = ArrayUtil.createFrom(list(OBJECT), Object.class);
		
		Object[]  obj02 = ArrayUtil.createFrom(list(STRING), Object.class);
		String[]  obj03 = ArrayUtil.createFrom(list(STRING), String.class);
		
		Object[]  obj04 = ArrayUtil.createFrom(list(LIST_FOO), Object.class);
		List<?>[] obj05 = ArrayUtil.createFrom(list(LIST_FOO), List.class);
		List<?>[] obj06 = ArrayUtil.createFrom(list(LIST_FOO), klass(LIST_FOO));
		Object[]  obj07 = ArrayUtil.createFrom(list(LIST_X_FOO), Object.class);
		List<?>[] obj08 = ArrayUtil.createFrom(list(LIST_X_FOO), List.class);
		Object[]  obj09 = ArrayUtil.createFrom(list(LIST_X_), Object.class);
		List<?>[] obj10 = ArrayUtil.createFrom(list(LIST_X_), List.class);
		Object[]  obj11 = ArrayUtil.createFrom(list(LIST), Object.class);
		List<?>[] obj12 = ArrayUtil.createFrom(list(LIST), List.class);
		List<?>[] obj13 = ArrayUtil.createFrom(list(LIST), klass(LIST));

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
//		String[] objX1 = ArrayUtil.createFrom(list(STRING), Object.class);
//		List[] objX2 = ArrayUtil.createFrom(list(STRING), Object.class);
	}
	
	@Test
	@SuppressWarnings("unused")
	public void test_create() {
		Object[]  obj01  = ArrayUtil.create(12, Object.class);
		String[]  obj01x = (String[]) ArrayUtil.create(12, (Class<?>) String.class);
		String[]  obj02  = ArrayUtil.create(16, new String[2]);
		// Ensure proper runtime component type is created
		String[]  obj02x  = (String[]) ArrayUtil.create(16, (Object[]) new String[2]);
		
		// The following must be tested manually
		// uncomment to make sure each line causes a compilation error
	}
	
	public final Integer[] EMPTY_INTEGER_ARRAY = array();

	public static <T> T[] array(T... elems) {
		return elems;
	}
    
	
    public void assertDeepEquals(Object[] a, Object[] b) {
    	assertTrue(Arrays.equals(a, b));
    }

	@Test
	public void test_concat() {
		Integer[] arr1 = array(0, 1, 2, 3, 4);
		Integer[] arr2 = array(10, 11, 12, 13, 14);
		
		assertDeepEquals(ArrayUtil.concat(arr1, arr2), array(0, 1, 2, 3, 4, 10, 11, 12, 13, 14));
		assertDeepEquals(ArrayUtil.concat(arr1, arr2, 0), array(0, 1, 2, 3, 4));
		assertDeepEquals(ArrayUtil.concat(arr1, arr2, 2), array(0, 1, 2, 3, 4, 10, 11));
		assertDeepEquals(ArrayUtil.concat(arr1, arr2, 5), array(0, 1, 2, 3, 4, 10, 11, 12, 13, 14));
	}
	
	@Test
	public void test_filter() {
		Integer[] arr1 = array(0, 1, 2, 3, 4);
		
		assertDeepEquals(ArrayUtil.filter(arr1, new Predicate<Integer>() {
			@Override
			public boolean evaluate(Integer obj) { 
				return true; 
			};
		}), array(0, 1, 2, 3, 4));
		                                                            

		assertDeepEquals(ArrayUtil.filter(arr1, new Predicate<Integer>() {
			@Override
			public boolean evaluate(Integer obj) { 
				return obj.intValue() % 2 == 0; 
			};
		}), array(0, 2, 4));
		
		assertDeepEquals(ArrayUtil.filter(arr1, new Predicate<Integer>() {
			@Override
			public boolean evaluate(Integer obj) { 
				return false; 
			};
		}), EMPTY_INTEGER_ARRAY);
	}
	
	@Test
	public void test_map() {
		List<String> list1 = Arrays.asList("a", "bc" , "Foo");
		String[] arr1 = ArrayUtil.createFrom(list1, String.class);
		Integer[] result1 = new Integer[] {1, 2, 3};
		
		final Function<String, Integer> eval1 = new Function<String, Integer>() {
			@Override
			public Integer evaluate(String obj) {
				return obj.length();
			}
		};
		
		ArrayUtil.map(list1, eval1);
		ArrayUtil.map(list1, eval1, Integer.class);
		ArrayUtil.map(list1, eval1, Number.class);
		
		ArrayUtil.map(arr1, eval1);
		ArrayUtil.map(arr1, eval1, Integer.class);
		ArrayUtil.map(arr1, eval1, Number.class);
		
		ArrayUtil.<String>map((String[])arr1, eval1);
		ArrayUtil.<String, Integer>map(arr1, eval1, Integer.class);
		ArrayUtil.<String, Number>map(arr1, eval1, Number.class);
		
		List<String> emptyList = CoreUtil.<List<String>>blindCast(Arrays.asList());
		
		asserArrayAreEqual(ArrayUtil.map(emptyList, eval1), new Object[0]);
		asserArrayAreEqual(ArrayUtil.map(list1, eval1), result1);
		asserArrayAreEqual(ArrayUtil.map(arr1, eval1), result1);
	}

	private void asserArrayAreEqual(Object[] arr1, Object[] arr2) {
		assertTrue(CoreUtil.areArrayEqual(arr2, arr1));
	}
	
}
