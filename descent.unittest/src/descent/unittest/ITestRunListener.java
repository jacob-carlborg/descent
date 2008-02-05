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

package descent.unittest;

import java.util.List;
  
/**
 * A listener interface for observing the execution of a test run.
 * <p>
 * Clients contributing to the 
 * <code>descent.unittest.testRunListener</code>
 * extension point implement this interface.
 * </p>
 */
 public interface ITestRunListener
 {
 	/**
 	 * A test run has started.
 	 * 
 	 * @param tests a list of all tetss that will be run
 	 */
	public void testRunStarted(List<ITestSpecification> tests);
	
	/**
 	 * A test run has ended.
	 *
	 * @param elapsedTime the total elapsed time of the test run
	 */
	public void testRunEnded(long elapsedTime);
	
	/**
	 * A test run has been stopped prematurely.
	 *
 	 * @param elapsedTime the time elapsed before the test run was stopped
	 */
	public void testRunStopped(long elapsedTime);
	
	/**
	 * An individual test has started.
	 * 
	 * @param test the test that started
	 */
	public void testStarted(ITestSpecification test);
	
	/**
	 * An individual test has ended.
	 * 
	 * @param test the test that was run
	 * @param result the result of running the test
	 */
	public void testEnded(ITestSpecification test, ITestResult result);
			
	/**
	 * The application instance performing the tests has terminated.
	 */
	public void testRunTerminated();
	
	/**
 	 * An individual test has been rerun. This is generally a user-initiated
 	 * action which occurs after a test run has ended.
	 * 
	 * @param test the test that was rerun
	 * @param result the result of re-running the test
	 */
	public void testReran(ITestSpecification test, ITestResult result);
}


