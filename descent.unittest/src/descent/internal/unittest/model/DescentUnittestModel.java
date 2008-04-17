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

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ListenerList;

import org.eclipse.swt.widgets.Display;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;

import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.debug.core.IDescentLaunchConfigurationConstants;

import descent.unittest.ITestResult;
import descent.unittest.ITestRunListener;
import descent.unittest.ITestSpecification;

import descent.internal.unittest.DescentUnittestPlugin;
import descent.internal.unittest.launcher.IUnittestLaunchConfigurationAttributes;
import descent.internal.unittest.ui.JUnitPreferencesConstants;
import descent.internal.unittest.ui.TestRunnerViewPart;

/**
 * Central registry for JUnit test runs.
 */
public final class DescentUnittestModel
{
	/**
	 * Notifies test run listeners added via the plugin extension point.
	 * 
	 * PERHAPS make the extension point mechanism more useful/robust -- that is,
	 *         extend the session-based interfaces to the external interface
	 */
	private static final class LegacyTestRunSessionListener implements
		ITestRunSessionListener
	{
	private TestRunSession fActiveTestRunSession;
	private ITestSessionListener fTestSessionListener;
	
	public void sessionAdded(TestRunSession testRunSession)
	{
		// Only serve one legacy ITestRunListener at a time, since they cannot distinguish between different concurrent test sessions:
		if (fActiveTestRunSession != null)
			return;
		
		fTestSessionListener = new ITestSessionListener()
		{
			public void sessionStarted()
			{
				ITestRunListener[] testRunListeners = DescentUnittestPlugin
						.getDefault().getTestRunListeners();
				for (int i = 0; i < testRunListeners.length; i++)
				{
					ITestRunListener testRunListener = testRunListeners[i];
					testRunListener.testRunStarted(fActiveTestRunSession
							.getTests());
				}	
			}
			
			public void sessionTerminated()
			{
				ITestRunListener[] testRunListeners = DescentUnittestPlugin
						.getDefault().getTestRunListeners();
				for (int i = 0; i < testRunListeners.length; i++)
				{
					ITestRunListener testRunListener = testRunListeners[i];
					testRunListener.testRunTerminated();
				}
				sessionRemoved(fActiveTestRunSession);
			}
			
			public void sessionStopped(long elapsedTime)
			{
				ITestRunListener[] testRunListeners = DescentUnittestPlugin
						.getDefault().getTestRunListeners();
				for (int i = 0; i < testRunListeners.length; i++)
				{
					ITestRunListener testRunListener = testRunListeners[i];
					testRunListener.testRunStopped(elapsedTime);
				}
				sessionRemoved(fActiveTestRunSession);
			}
			
			public void sessionEnded(long elapsedTime)
			{
				ITestRunListener[] testRunListeners = DescentUnittestPlugin
						.getDefault().getTestRunListeners();
				for (int i = 0; i < testRunListeners.length; i++)
				{
					ITestRunListener testRunListener = testRunListeners[i];
					testRunListener.testRunEnded(elapsedTime);
				}
				sessionRemoved(fActiveTestRunSession);
			}
			
			public void testStarted(TestCaseElement testCaseElement)
			{
				ITestRunListener[] testRunListeners = DescentUnittestPlugin
						.getDefault().getTestRunListeners();
				for (int i = 0; i < testRunListeners.length; i++)
				{
					ITestRunListener testRunListener = testRunListeners[i];
					testRunListener.testStarted(testCaseElement.getTestSpecification());
				}
			}
			
			public void testEnded(TestCaseElement testCaseElement,
					ITestResult result)
			{
				ITestRunListener[] testRunListeners = DescentUnittestPlugin
						.getDefault().getTestRunListeners();
				for (int i = 0; i < testRunListeners.length; i++)
				{
					ITestRunListener testRunListener = testRunListeners[i];
					testRunListener.testEnded(testCaseElement.getTestSpecification(),
							testCaseElement.getResult());
				}	
			}

			public void testReran(TestCaseElement testCaseElement,
					ITestResult result)
			{
				ITestRunListener[] testRunListeners = DescentUnittestPlugin
						.getDefault().getTestRunListeners();
				for (int i = 0; i < testRunListeners.length; i++)
				{
					ITestRunListener testRunListener = testRunListeners[i];
					testRunListener.testReran(testCaseElement.getTestSpecification(),
							testCaseElement.getResult());
				}
			}
		};
		
		fActiveTestRunSession = testRunSession;
		fActiveTestRunSession.addTestSessionListener(fTestSessionListener);
	}
	
	public void sessionRemoved(TestRunSession testRunSession)
	{
		if (fActiveTestRunSession == testRunSession)
		{
			fActiveTestRunSession
					.removeTestSessionListener(fTestSessionListener);
			fTestSessionListener = null;
			fActiveTestRunSession = null;
		}
	}
}
	//--------------------------------------------------------------------------
	// Test run activation
	
	/**
	 * This method is called by 
	 * (@link descent.internal.unittest.launcher.UnittestLaunchConfiguration#launch}
	 * 
	 * This is where the test runner view is connected as well as the session
	 * itself is started (i.e. this method will eventually create the
	 * FluteApplicationInstance that will connect to the fluted application
	 * for IPC & actually run the tests).
	 * 
	 * While I'd love to do this with a launch listener (as in the initial
	 * version), I can't seemt o get that to work, and if there are problems
	 * with multiple versions of the same launch causing errors, I can check
	 * since I'm passed the launch itself. This also has the advantage of making
	 * more sense to me, since the whole thing of using changes to the launch
	 * to track stuff justs seems wierd to me (since it doesn't actually care
	 * aboout changes to the launch but instead the triggering of a specific
	 * state).
	 */
	public void notifyLaunch(final ILaunch launch, 
			final List<ITestSpecification> tests)
	{
		ILaunchConfiguration config = launch.getLaunchConfiguration();
		if (config == null)
			return;
		
		// test whether the launch defines the JUnit attributes
		String portStr = launch.getAttribute(IUnittestLaunchConfigurationAttributes.PORT_ATTR);
		String projectStr = launch.getAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME);
		if (portStr == null || projectStr == null)
			return;
		
		final int port = Integer.parseInt(portStr);
		final IJavaProject launchedProject = getJavaProject(projectStr);
		
		getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				connectTestRunner(launch, launchedProject, /* TODO port */ 30587, tests);
			}
		});
	}
	
	public static IJavaProject getJavaProject(String projectName)
	{
		if (projectName != null) {
			projectName = projectName.trim();
			if (projectName.length() > 0) {
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
				IJavaProject javaProject = JavaCore.create(project);
				if (javaProject != null && javaProject.exists()) {
					return javaProject;
				}
			}
		}
		return null;
	}
	
	private void connectTestRunner(ILaunch launch, IJavaProject project,
			int port, List<ITestSpecification> tests)
	{
		showTestRunnerViewPartInActivePage(findTestRunnerViewPartInActivePage());

		//JTODO: Do notifications have to be sent in UI thread? 
		// Check concurrent access to fTestRunSessions (no problem inside asyncExec())
		int maxCount = DescentUnittestPlugin.getDefault().getPreferenceStore().
				getInt(JUnitPreferencesConstants.MAX_TEST_RUNS);
		int toDelete = fTestRunSessions.size() - maxCount;
		while (toDelete > 0)
		{
			toDelete--;
			TestRunSession session = (TestRunSession) fTestRunSessions.removeLast();
			notifyTestRunSessionRemoved(session);
		}

		TestRunSession testRunSession = new TestRunSession(project, port, launch, tests);
		fTestRunSessions.addFirst(testRunSession);
		notifyTestRunSessionAdded(testRunSession);
	}
	
	private static TestRunnerViewPart showTestRunnerViewPartInActivePage(
			TestRunnerViewPart testRunner)
	{
		IWorkbenchPart activePart = null;
		IWorkbenchPage page = null;
		try
		{
			// JTODO: have to force the creation of view part contents 
			// otherwise the UI will not be updated
			if (testRunner != null && testRunner.isCreated())
				return testRunner;
			page = DescentUnittestPlugin.getActivePage();
			if (page == null)
				return null;
			activePart = page.getActivePart();
			// show the result view if it isn't shown yet
			return (TestRunnerViewPart) page
					.showView(TestRunnerViewPart.NAME);
		}
		catch (PartInitException pie)
		{
			/*************************
             *      _,..---..,_      *
             *  ,-"`    .'.    `"-,  *
             * ((      '.'.'      )) *
             *  `'-.,_   '   _,.-'`  *
             *    `\  `"""""`  /`    *
             *      `""-----""`      *
             *************************
             *      Mmmm... Pie!     *
             *************************/
			DescentUnittestPlugin.log(pie);
			return null;
		}
		finally
		{
			//restore focus stolen by the creation of the result view
			if (page != null && activePart != null)
				page.activate(activePart);
		}
	}
	
	private static TestRunnerViewPart findTestRunnerViewPartInActivePage()
	{
		IWorkbenchPage page = DescentUnittestPlugin.getActivePage();
		if (page == null)
			return null;
		return (TestRunnerViewPart) page.findView(TestRunnerViewPart.NAME);
	}
	
	private static Display getDisplay()
	{
		Display display = Display.getCurrent();
		if (display == null)
		{
			display = Display.getDefault();
		}
		return display;
	}
	
	//--------------------------------------------------------------------------
	// Listener management
	
	private final ListenerList fTestRunSessionListeners = new ListenerList();
	private final LinkedList<TestRunSession> fTestRunSessions = new LinkedList<TestRunSession>();
	
	/**
	 * Starts the model (called by the {@link DescentUnittestPlugin} on startup).
	 */
	public void start()
	{
		fTestRunSessionListeners.add(new LegacyTestRunSessionListener());
	}
	
	/**
	 * Stops the model (called by the {@link DescentUnittestPlugin} on shutdown).
	 */
	public void stop()
	{
		// Nothing to do
	}
	
	public void addTestRunSessionListener(ITestRunSessionListener listener)
	{
		fTestRunSessionListeners.add(listener);
	}

	public void removeTestRunSessionListener(ITestRunSessionListener listener)
	{
		fTestRunSessionListeners.remove(listener);
	}
	
	/**
	 * @return a list of active {@link TestRunSession}s. The list is a copy of
	 *         the internal data structure and modifications do not affect the
	 *         global list of active sessions. The list is sorted by age, youngest first.  
	 */
	public List<TestRunSession> getTestRunSessions()
	{
		return new ArrayList<TestRunSession>(fTestRunSessions);
	}
	
	/**
	 * Removes the given {@link TestRunSession} and notifies all registered
	 * {@link ITestRunSessionListener}s.
	 * <p>
	 * <b>To be called in the UI thread only!</b>
	 * </p>
	 * 
	 * @param testRunSession the session to remove
	 */
	public void removeTestRunSession(TestRunSession testRunSession)
	{
		boolean existed = fTestRunSessions.remove(testRunSession);
		if (existed)
		{
			notifyTestRunSessionRemoved(testRunSession);
		}
	}
	
	private void notifyTestRunSessionRemoved(TestRunSession testRunSession)
	{
		testRunSession.stopTestRun();
		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		launchManager.removeLaunch(testRunSession.getLaunch());

		Object[] listeners = fTestRunSessionListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i)
		{
			((ITestRunSessionListener) listeners[i]).sessionRemoved(testRunSession);
		}
	}
	
	private void notifyTestRunSessionAdded(TestRunSession testRunSession)
	{
		Object[] listeners = fTestRunSessionListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i)
		{
			((ITestRunSessionListener) listeners[i]).sessionAdded(testRunSession);
		}
	}
}
