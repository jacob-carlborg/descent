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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ListenerList;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;

import descent.core.ICompilationUnit;
import descent.core.IJavaProject;
import descent.internal.unittest.flute.FluteApplicationInstance;
import descent.internal.unittest.model.TestElement.Status;

import descent.unittest.ITestResult;
import descent.unittest.ITestRunListener;
import descent.unittest.ITestSpecification;

public class TestRunSession
{
	private final IJavaProject fProject;
	private final ILaunch fLaunch;
	private final String fLaunchConfigName;
	
	/** The client is used to connect to the tested application and run stuff
	 * (this client implementation actually just forwards the work to the
	 * FluteApplicationInstance class, but it's easier this way than a major
	 * refactor to remove the class).
	 */
	private final RemoteTestRunnerClient fTestRunnerClient;
	
	private final ListenerList/*<ITestSessionListener>*/ fSessionListeners;
	
	/**
	 * The model root.
	 */
	private final TestRoot fTestRoot;
	
	/**
	 * Map from testId to testElement. Can't map ITestSpecification to
	 * TestElement, sicne this includes test suites
	 */
	private final HashMap<String, TestElement> fIdToTest;
	
 	/**
 	 * Number of tests started during this test run.
 	 */
	volatile int fStartedCount = 0;
	
	/**
	 * Number of tests ignored during this test run.
	 */
	// PERHAPS ignored tests (they're totally unsupported)
	volatile int fIgnoredCount = 0; 
	
	/**
	 * Number of errors during this test run.
	 */
	volatile int fErrorCount = 0;
	
	/**
	 * Number of failures during this test run.
	 */
	volatile int fFailureCount = 0;
	
	/**
	 * Total number of tests to run.
	 */
	volatile int fTotalCount = 0;
	
	/**
	 * Start time in millis.
	 */
	volatile long fStartTime;
	volatile boolean fIsRunning;
	
	volatile boolean fIsStopped;
	

	public TestRunSession(IJavaProject testedProject, FluteApplicationInstance app,
	        ILaunch launch, final List<ITestSpecification> tests)
	{
	    assert(null != app);
		assert(null != testedProject);
		assert(null != launch);
		
		fProject= testedProject;
		fLaunch= launch;
		
		ILaunchConfiguration launchConfiguration= launch.getLaunchConfiguration();
		if (launchConfiguration != null)
		 	fLaunchConfigName= launchConfiguration.getName();
		else
		 	fLaunchConfigName= testedProject.getElementName();
		
		fTestRoot= new TestRoot();
		fIdToTest= new HashMap<String, TestElement>();
		fIdToTest.put(fTestRoot.getId(), fTestRoot);
		fTotalCount = 0;
		
		// Add the run listener that translates stuff to the session listeners
		List<ITestRunListener> listeners = new ArrayList<ITestRunListener>();
		listeners.add(new TestSessionNotifier());
		
		fTestRunnerClient= new RemoteTestRunnerClient(app, tests, listeners);
		fSessionListeners= new ListenerList();
		
		// PERHAPS error handling might be nice...
		(new Thread(new Runnable()
			{
				public void run()
				{
					createTestTree(tests);
					if(fTestRunnerClient.isConnected())
						fTestRunnerClient.run();
				}
			}
		)).start();
	}
	
	public List<ITestSpecification> getTests()
	{
		return fTestRunnerClient.getTests();
	}
	
	public TestRoot getTestRoot() {
		return fTestRoot;
	}

	public IJavaProject getAssociatedProject() {
		return fProject;
	}
	
	public ILaunch getLaunch() {
		return fLaunch;
	}
	
	public String getTestRunName() {
		return fLaunchConfigName;
	}
	
	public int getErrorCount() {
		return fErrorCount;
	}

	public int getFailureCount() {
		return fFailureCount;
	}

	public int getStartedCount() {
		return fStartedCount;
	}

	public int getIgnoredCount() {
		return fIgnoredCount;
	}
	
	public int getTotalCount() {
		return fTotalCount;
	}

	public long getStartTime() {
		return fStartTime;
	}
	
	/**
	 * @return <code>true</code> iff the session has been stopped or terminated
	 */
	public boolean isStopped() {
		return fIsStopped;
	}

	public void addTestSessionListener(ITestSessionListener listener) {
		fSessionListeners.add(listener);
	}
	
	public void removeTestSessionListener(ITestSessionListener listener) {
		fSessionListeners.remove(listener);
	}
	
	public void stopTestRun() {
		if (isRunning() || ! isKeptAlive())
			fIsStopped= true;
		fTestRunnerClient.stopTest();
	}

	/**
	 * @return <code>true</code> iff the runtime VM of this test session is still alive 
	 */
	public boolean isKeptAlive() {
		return fTestRunnerClient.isRunning() && ILaunchManager.DEBUG_MODE.equals(getLaunch().getLaunchMode());
	}

	/**
	 * @return <code>true</code> iff this session has been started, but not ended nor stopped nor terminated
	 */
	public boolean isRunning() {
		return fIsRunning;
	}
	
	/**
	 * @return an array of all failed test elments, including test cases and
	 *         test suites.
	 */
	public TestElement[] getAllFailedTestElements()
	{
		ArrayList<TestElement> failures = new ArrayList<TestElement>();
		addFailures(failures, getTestRoot());
		return (TestElement[]) failures.toArray(new TestElement[failures.size()]);
	}
	
	// Helper method to recursively add failed tests to the list
	private void addFailures(ArrayList<TestElement> failures,
			TestElement testElement)
	{
		if (testElement.getStatus().isErrorOrFailure())
			failures.add(testElement);
		
		if (testElement instanceof TestSuiteElement)
		{
			TestSuiteElement testSuiteElement= (TestSuiteElement) testElement;
			TestElement[] children= testSuiteElement.getChildren();
			for (int i= 0; i < children.length; i++)
				addFailures(failures, children[i]);
		}
	}
	
	/**
	 * @param testId 
	 * @param launchMode 
	 * @return <code>false</code> iff the rerun could not be started
	 * @throws CoreException 
	 */
	public boolean rerunTest(String testId, String launchMode) 
			throws CoreException
	{
		// TODO reruns, keeeping in mind the testId could refer to a
		// TestSuiteElement instead of just a TestcaseElement
		
		/*TestElement testElement = fIdToTest.get(testId);
		
		if(null == testElement)
			return false;
		
		TestCaseElement testCaseElement = (TestCaseElement) testElement;
		ITestSpecification test = testCaseElement.getTestSpecification();
		
		if (isKeptAlive())
		{
			Status status= ((TestCaseElement) getTestElement(testId)).getStatus();
			if (status == Status.ERROR) {
				fErrorCount--;
			} else if (status == Status.FAILURE) {
				fFailureCount--;
			}
			fTestRunnerClient.rerunTest(test);
			return true;
			
		}
		else if (getLaunch() != null)
		{
			// run the selected test using the previous launch configuration
			ILaunchConfiguration launchConfiguration= getLaunch().getLaunchConfiguration();
			if (launchConfiguration != null) {

				String name= className;
				if (testName != null) 
					name+= "."+testName; //$NON-NLS-1$
				String configName= Messages.format(JUnitMessages.TestRunnerViewPart_configName, name); 
				ILaunchConfigurationWorkingCopy tmp= launchConfiguration.copy(configName); 
				// fix for bug: 64838  unittest view run single test does not use correct class [JUnit] 
				tmp.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, className);
				// reset the container
				tmp.setAttribute(JUnitBaseLaunchConfiguration.LAUNCH_CONTAINER_ATTR, ""); //$NON-NLS-1$
				if (testName != null) {
					tmp.setAttribute(JUnitBaseLaunchConfiguration.TESTNAME_ATTR, testName);
					//	String args= "-rerun "+testId;
					//	tmp.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, args);
				}
				tmp.launch(launchMode, null);	
				return true;
			}
		} */
		
		return false;
	}
	
	public TestElement getTestElement(String id)
	{
		return (TestElement) fIdToTest.get(id);
	}
	
	//--------------------------------------------------------------------------
	// Test tree management (internal)
	private void createTestTree(List<ITestSpecification> tests)
	{
		// PERHAPS a more structured (package-based) approach?
		
		Map<ICompilationUnit, TestSuiteElement> nodes = 
				new HashMap<ICompilationUnit, TestSuiteElement>();
		
		for(ITestSpecification test : tests)
		{
			ICompilationUnit module = test.getDeclaration().getCompilationUnit();
			
			TestSuiteElement parent;
			if(!nodes.keySet().contains(module))
			{
				parent = new TestSuiteElement(fTestRoot, 
						module.getFullyQualifiedName(),
						module.getFullyQualifiedName());
				nodes.put(module, parent);
				fIdToTest.put(parent.getId(), parent);
			}
			else
			{
				parent = nodes.get(module);
			}
			
			TestCaseElement node = new TestCaseElement(parent, test);
			fIdToTest.put(test.getId(), node);
		}
	}
	
	//--------------------------------------------------------------------------
	// Notification framework (man, are there a LOT of listeners in this...)
	
	/**
	 * An {@link ITestRunListener} that listens to events from the
	 * {@link RemoteTestRunnerClient} and translates them into high-level model
	 * events (broadcasted to {@link ITestSessionListener}s).
	 */
	private class TestSessionNotifier implements ITestRunListener
	{
		public void testRunStarted(List<ITestSpecification> tests)
		{	
			fStartedCount= 0;
			fIgnoredCount= 0;
			fFailureCount= 0;
			fErrorCount= 0;
			fTotalCount= tests.size();
			
			fStartTime= System.currentTimeMillis();
			fIsRunning= true;
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).sessionStarted();
			}
		}
		
		public void testRunEnded(long elapsedTime)
		{
			fIsRunning= false;
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).sessionEnded(elapsedTime);
			}
		}
		
		public void testRunStopped(long elapsedTime)
		{
			fIsRunning= false;
			fIsStopped= true;
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).sessionStopped(elapsedTime);
			}
		}
		
		public void testRunTerminated()
		{
			fIsRunning= false;
			fIsStopped= true;
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).sessionTerminated();
			}
		}
		
		public void testStarted(ITestSpecification test)
		{
			TestElement testElement = fIdToTest.get(test.getId());
			TestCaseElement testCaseElement = (TestCaseElement) testElement;
			testCaseElement.setStatus(Status.RUNNING);
			
			fStartedCount++;
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).testStarted(testCaseElement);
			}
		}
		
		public void testEnded(ITestSpecification test, ITestResult result)
		{
			TestElement testElement = fIdToTest.get(test.getId());
			TestCaseElement testCaseElement = (TestCaseElement) testElement;

			Status status = Status.statusOf(result);
			testCaseElement.setResult(result);
			
			if (status.isError())
				fErrorCount++;
			else if(status.isFailure())
				fFailureCount++;
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).testEnded(testCaseElement, result);
			}
		}

		public void testReran(ITestSpecification test, ITestResult result)
		{
			TestElement testElement = fIdToTest.get(test.getId());
			TestCaseElement testCaseElement = (TestCaseElement) testElement;

			Status status = Status.statusOf(result);
			testCaseElement.setResult(result);
			
			if (status.isError())
				fErrorCount++;
			else if(status.isFailure())
				fFailureCount++;
			
			Object[] listeners= fSessionListeners.getListeners();
			for (int i= 0; i < listeners.length; ++i) {
				((ITestSessionListener) listeners[i]).testReran(testCaseElement, result);
			}
		}
	}
}
