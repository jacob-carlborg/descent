/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.unittest.model;

import descent.unittest.ITestRunListener;

/**
 * Extends ITestRunListener with
 * <ul>
 * <li>a call back to trace the test contents, and</li>
 * <li>additional arguments to {@link #testFailed(int, String, String, String)}
 * and {@link #testReran(String, String, String, int, String)} with expected and
 * actual value. The replaced methods from {@link ITestRunListener} are <i>not</i>
 * called on {@link ITestRunListener2}</li>
 * </ul>
 */
public interface ITestRunListener2 extends ITestRunListener {

	/**
	 * Information about a member of the test suite that is about to be run. The
	 * format of the string is:
	 * 
	 * <pre>
	 *  testId,isSuite,testcount
	 *  
	 *  testId: a unique id for the test
	 *  isSuite: true or false depending on whether the test is a suite
	 *  testCount: an integer indicating the number of tests 
	 *  
	 *  Example: &quot;324968,testPass(unittest.tests.MyTest),false,1&quot;
	 * </pre>
	 * 
	 * @param description a string describing a tree entry
	 * 
	 * @see MessageIds#TEST_TREE
	 */
	public void testTreeEntry(String description);
}
