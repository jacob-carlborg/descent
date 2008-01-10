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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;

import org.eclipse.swt.widgets.Display;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchManager;

import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.JavaCore;

import descent.unittest.ITestRunListener;

import descent.internal.unittest.DescentUnittestPlugin;
import descent.internal.unittest.launcher.JUnitBaseLaunchConfiguration;
import descent.internal.unittest.model.TestElement.Status;
import descent.internal.unittest.ui.JUnitPreferencesConstants;
import descent.internal.unittest.ui.TestRunnerViewPart;

/**
 * Central registry for JUnit test runs.
 */
public final class JUnitModel {
	
	private final class JUnitLaunchListener implements ILaunchListener {

		/**
		 * Used to track new launches. We need to do this
		 * so that we only attach a TestRunner once to a launch.
		 * Once a test runner is connected, it is removed from the set.
		 */
		private HashSet fTrackedLaunches= new HashSet(20);

		/*
		 * @see ILaunchListener#launchAdded(ILaunch)
		 */
		public void launchAdded(ILaunch launch) {
			fTrackedLaunches.add(launch);
		}

		/*
		 * @see ILaunchListener#launchRemoved(ILaunch)
		 */
		public void launchRemoved(final ILaunch launch) {
			fTrackedLaunches.remove(launch);
			//JTODO: story for removing old test runs?
		}

		/*
		 * @see ILaunchListener#launchChanged(ILaunch)
		 */
		public void launchChanged(final ILaunch launch) {
			if (!fTrackedLaunches.contains(launch))
				return;
		
			ILaunchConfiguration config= launch.getLaunchConfiguration();
			if (config == null)
				return;
			
			// test whether the launch defines the JUnit attributes
			String portStr= launch.getAttribute(JUnitBaseLaunchConfiguration.PORT_ATTR);
			String projectStr= launch.getAttribute(JUnitBaseLaunchConfiguration.TESTPROJECT_ATTR);
			if (portStr == null || projectStr == null)
				return;
			
			IJavaElement element= JavaCore.create(projectStr);
			if (! (element instanceof IJavaProject))
				return;
			
			final int port= Integer.parseInt(portStr);
			final IJavaProject launchedProject= (IJavaProject) element;
			fTrackedLaunches.remove(launch);
			getDisplay().asyncExec(new Runnable() {
				public void run() {
					connectTestRunner(launch, launchedProject, port);
				}
			});
		}

		private void connectTestRunner(ILaunch launch, IJavaProject project, int port) {
			showTestRunnerViewPartInActivePage(findTestRunnerViewPartInActivePage());
			
			//JTODO: Do notifications have to be sent in UI thread? 
			// Check concurrent access to fTestRunSessions (no problem inside asyncExec())
			int maxCount= DescentUnittestPlugin.getDefault().getPreferenceStore().getInt(JUnitPreferencesConstants.MAX_TEST_RUNS);
			int toDelete= fTestRunSessions.size() - maxCount;
			while (toDelete > 0) {
				toDelete--;
				TestRunSession session= (TestRunSession) fTestRunSessions.removeLast();
				notifyTestRunSessionRemoved(session);
			}
			
			TestRunSession testRunSession= new TestRunSession(project, port, launch);
			fTestRunSessions.addFirst(testRunSession);
			notifyTestRunSessionAdded(testRunSession);
		}

		private TestRunnerViewPart showTestRunnerViewPartInActivePage(TestRunnerViewPart testRunner) {
			IWorkbenchPart activePart= null;
			IWorkbenchPage page= null;
			try {
				// JTODO: have to force the creation of view part contents 
				// otherwise the UI will not be updated
				if (testRunner != null && testRunner.isCreated())
					return testRunner;
				page= DescentUnittestPlugin.getActivePage();
				if (page == null)
					return null;
				activePart= page.getActivePart();
				//	show the result view if it isn't shown yet
				return (TestRunnerViewPart) page.showView(TestRunnerViewPart.NAME);
			} catch (PartInitException pie) {
				DescentUnittestPlugin.log(pie);
				return null;
			} finally{
				//restore focus stolen by the creation of the result view
				if (page != null && activePart != null)
					page.activate(activePart);
			}
		}

		private TestRunnerViewPart findTestRunnerViewPartInActivePage() {
			IWorkbenchPage page= DescentUnittestPlugin.getActivePage();
			if (page == null)
				return null;
			return (TestRunnerViewPart) page.findView(TestRunnerViewPart.NAME);
		}

		private Display getDisplay() {
			Display display= Display.getCurrent();
			if (display == null) {
				display= Display.getDefault();
			}
			return display;
		}
	}

	private static final class LegacyTestRunSessionListener implements ITestRunSessionListener {
		private TestRunSession fActiveTestRunSession;
		private ITestSessionListener fTestSessionListener;
		
		public void sessionAdded(TestRunSession testRunSession) {
			// Only serve one legacy ITestRunListener at a time, since they cannot distinguish between different concurrent test sessions:
			if (fActiveTestRunSession != null)
				return;
			
			fActiveTestRunSession= testRunSession;
			
			fTestSessionListener= new ITestSessionListener() {
				public void testAdded(TestElement testElement) {
				}
				
				public void sessionStarted() {
					ITestRunListener[] testRunListeners= DescentUnittestPlugin.getDefault().getTestRunListeners();
					for (int i= 0; i < testRunListeners.length; i++) {
						ITestRunListener testRunListener= testRunListeners[i];
						testRunListener.testRunStarted(fActiveTestRunSession.getTotalCount());
					}
				}
				public void sessionTerminated() {
					ITestRunListener[] testRunListeners= DescentUnittestPlugin.getDefault().getTestRunListeners();
					for (int i= 0; i < testRunListeners.length; i++) {
						ITestRunListener testRunListener= testRunListeners[i];
						testRunListener.testRunTerminated();
					}
					sessionRemoved(fActiveTestRunSession);
				}
				public void sessionStopped(long elapsedTime) {
					ITestRunListener[] testRunListeners= DescentUnittestPlugin.getDefault().getTestRunListeners();
					for (int i= 0; i < testRunListeners.length; i++) {
						ITestRunListener testRunListener= testRunListeners[i];
						testRunListener.testRunStopped(elapsedTime);
					}
					sessionRemoved(fActiveTestRunSession);
				}
				public void sessionEnded(long elapsedTime) {
					ITestRunListener[] testRunListeners= DescentUnittestPlugin.getDefault().getTestRunListeners();
					for (int i= 0; i < testRunListeners.length; i++) {
						ITestRunListener testRunListener= testRunListeners[i];
						testRunListener.testRunEnded(elapsedTime);
					}
					sessionRemoved(fActiveTestRunSession);
				}
				public void testStarted(TestCaseElement testCaseElement) {
					ITestRunListener[] testRunListeners= DescentUnittestPlugin.getDefault().getTestRunListeners();
					for (int i= 0; i < testRunListeners.length; i++) {
						ITestRunListener testRunListener= testRunListeners[i];
						testRunListener.testStarted(testCaseElement.getId(), testCaseElement.getTestName());
					}
				}
				
				public void testFailed(TestElement testElement, Status status, String trace, String expected, String actual) {
					ITestRunListener[] testRunListeners= DescentUnittestPlugin.getDefault().getTestRunListeners();
					for (int i= 0; i < testRunListeners.length; i++) {
						ITestRunListener testRunListener= testRunListeners[i];
						testRunListener.testFailed(status.getOldCode(), testElement.getId(), testElement.getTestName(), trace);
					}
				}
				
				public void testEnded(TestCaseElement testCaseElement) {
					ITestRunListener[] testRunListeners= DescentUnittestPlugin.getDefault().getTestRunListeners();
					for (int i= 0; i < testRunListeners.length; i++) {
						ITestRunListener testRunListener= testRunListeners[i];
						testRunListener.testEnded(testCaseElement.getId(), testCaseElement.getTestName());
					}
				}
				
				public void testReran(TestCaseElement testCaseElement, Status status, String trace, String expectedResult, String actualResult) {
					ITestRunListener[] testRunListeners= DescentUnittestPlugin.getDefault().getTestRunListeners();
					for (int i= 0; i < testRunListeners.length; i++) {
						ITestRunListener testRunListener= testRunListeners[i];
						testRunListener.testReran(testCaseElement.getId(), testCaseElement.getTestMethodName(), status.getOldCode(), trace);
					}
				}
			};
			fActiveTestRunSession.addTestSessionListener(fTestSessionListener);
		}

		public void sessionRemoved(TestRunSession testRunSession) {
			if (fActiveTestRunSession == testRunSession) {
				fActiveTestRunSession.removeTestSessionListener(fTestSessionListener);
				fTestSessionListener= null;
				fActiveTestRunSession= null;
			}
		}
	}
	
	private final ListenerList fTestRunSessionListeners= new ListenerList();
	/**
	 * Active test run sessions, youngest first.
	 */
	private final LinkedList/*<TestRunSession>*/ fTestRunSessions= new LinkedList();
	private final ILaunchListener fLaunchListener= new JUnitLaunchListener();

	/**
	 * Starts the model (called by the {@link DescentUnittestPlugin} on startup).
	 */
	public void start() {
		ILaunchManager launchManager= DebugPlugin.getDefault().getLaunchManager();
		launchManager.addLaunchListener(fLaunchListener);
		
		addTestRunSessionListener(new LegacyTestRunSessionListener());
	}

	/**
	 * Stops the model (called by the {@link DescentUnittestPlugin} on shutdown).
	 */
	public void stop() {
		ILaunchManager launchManager= DebugPlugin.getDefault().getLaunchManager();
		launchManager.removeLaunchListener(fLaunchListener);
	}
	
	
	public void addTestRunSessionListener(ITestRunSessionListener listener) {
		fTestRunSessionListeners.add(listener);
	}
	
	public void removeTestRunSessionListener(ITestRunSessionListener listener) {
		fTestRunSessionListeners.remove(listener);
	}
	
	
	/**
	 * @return a list of active {@link TestRunSession}s. The list is a copy of
	 *         the internal data structure and modifications do not affect the
	 *         global list of active sessions. The list is sorted by age, youngest first.  
	 */
	public List getTestRunSessions() {
		return new ArrayList(fTestRunSessions);
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
	public void removeTestRunSession(TestRunSession testRunSession) {
		boolean existed= fTestRunSessions.remove(testRunSession);
		if (existed) {
			notifyTestRunSessionRemoved(testRunSession);
		}
	}
	
	private void notifyTestRunSessionRemoved(TestRunSession testRunSession) {
		testRunSession.stopTestRun();
		ILaunchManager launchManager= DebugPlugin.getDefault().getLaunchManager();
		launchManager.removeLaunch(testRunSession.getLaunch());
		
		Object[] listeners = fTestRunSessionListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((ITestRunSessionListener) listeners[i]).sessionRemoved(testRunSession);
		}
	}
	
	private void notifyTestRunSessionAdded(TestRunSession testRunSession) {
		Object[] listeners = fTestRunSessionListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((ITestRunSessionListener) listeners[i]).sessionAdded(testRunSession);
		}
	}

}
