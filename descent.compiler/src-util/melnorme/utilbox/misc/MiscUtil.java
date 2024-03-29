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
package melnorme.utilbox.misc;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import melnorme.utilbox.core.Predicate;

public class MiscUtil {
	
	public static <T> Predicate<T> getNotNullPredicate() {
		return new NotNullPredicate<T>();
	}
	
	public static final class NotNullPredicate<T> implements Predicate<T> {
		@Override
		public boolean evaluate(T obj) {
			return obj != null;
		}
	}
	
	public static <T> Predicate<T> getIsNullPredicate() {
		return new IsNullPredicate<T>();
	}
	
	public static final class IsNullPredicate<T> implements Predicate<T> {
		@Override
		public boolean evaluate(T obj) {
			return obj == null;
		}
	}
	
	/** Loads given klass. */
	public static void loadClass(Class<?> klass) {
		try {
			// use klass.getClassLoader(), in case klass cannot be loaded in the current (caller) classloader
			// it can happen in OSGi runtimes for example
			Class.forName(klass.getName(), true, klass.getClassLoader());
		} catch (ClassNotFoundException e) {
			assertFail();
		}
	}
	
	/** Combines two hash codes to make a new one. */
	public static int combineHashCodes(int hashCode1, int hashCode2) {
		return hashCode1 * 17 + hashCode2;
	}
	
	/** Runs a shell command as a Process and waits for it to terminate. 
	 * Assumes the process does not read input. 
	 * @return exit value of the process */
	public static int runShellCommand(String directory, String cmd, String... args) throws IOException {
		ProcessBuilder procBuilder = new ProcessBuilder();
		ArrayList<String> cmdList = new ArrayList<String>(args.length+1);
		cmdList.add(cmd);
		cmdList.addAll(Arrays.asList(args));
		procBuilder.command(cmdList);
		procBuilder.redirectErrorStream(true);
		procBuilder.directory(new File(directory));
	
		Process process = procBuilder.start();
		// read proccess's stdout and stderr, so it doesn't get stuck in I/O
		StreamUtil.readAllBytesFromStream(process.getInputStream());
		
		try {
			return process.waitFor();
		} catch (InterruptedException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	/** Counts the active flags in the given bitfield */
	public static int countActiveFlags(int bitfield, int[] flags) {
		int count = 0;
		for (int i = 0; i < flags.length; i++) {
			if((bitfield & flags[i]) != 0)
				count++;
		}
		return count;
	}
	
	/** Returns the first element of objs array that is not null.
	 * At least one element must be not null. */
	public static <T> T firstNonNull(T... objs) {
		for (int i = 0; i < objs.length; i++) {
			if(objs[i] != null)
				return objs[i];
		}
		assertFail();
		return null;
	}
	
	/** Convenience method for extracting the element of a single element collection . */
	public static <T> T getSingleElement(Collection<T> singletonDefunits) {
		assertTrue(singletonDefunits.size() == 1);
		return singletonDefunits.iterator().next();
	}
	
	/** Returns a copy of given collection, synchs on the given collection. */
	@Deprecated
	public static <T> List<T> synchronizedCreateCopy(Collection<T> collection) {
		ArrayList<T> newCollection;
		synchronized (collection) {
			newCollection = new ArrayList<T>(collection);
		}
		return newCollection;
	}
	
	/** Synchronizes on the given collection, and returns a copy suitable for iteration. */
	public static <T> Iterable<T> synchronizedCreateIterable(Collection<T> collection) {
		Iterable<T> iterable;
		synchronized (collection) {
			iterable = new ArrayList<T>(collection);
		}
		return iterable;
	}
	
	/** Sleeps current thread for given millis amount. 
	 * If interrupted throws an unchecked exception. */
	public static void sleepUnchecked(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
}
