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

import descent.unittest.ITestResult;

/**
 * A listener interface for observing the execution of a test session 
 * (initial run and reruns).
 */
public interface ITestSessionListener
{
 	/**
 	 * A test run has started.
 	 */
	public void sessionStarted();
	
	/**
 	 * A test run has ended.
	 *
	 * @param elapsedTime the total elapsed time of the test run
	 */
	public void sessionEnded(long elapsedTime);
	
	/**
	 * A test run has been stopped prematurely.
	 *
 	 * @param elapsedTime the time elapsed before the test run was stopped
	 */
	public void sessionStopped(long elapsedTime);
	
	/**
	 * The application instance performing the tests has terminated.
	 */
	public void sessionTerminated();
	
	/**
	 * An individual test has started.
	 * 
	 * @param testCaseElement the test
	 */
	public void testStarted(TestCaseElement testCaseElement);
	
	/**
	 * An individual test has ended.
	 * 
	 * @param testCaseElement the test
	 * @param result the result of running the test (Status can be gleamed from
	 *     this using the TestElement.Status.startusOf(ITestElement) mehod.
	 */
	public void testEnded(TestCaseElement testCaseElement, ITestResult result);
	
	/**
 	 * An individual test has been rerun.
	 * 
	 * @param testCaseElement the test
	 * @param result the result of running the test (Status can be gleamed from
	 *     this using the TestElement.Status.startusOf(ITestElement) mehod.
	 */
	public void testReran(TestCaseElement testCaseElement, ITestResult result);
}
