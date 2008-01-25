/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package descent.internal.unittest.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

import descent.internal.unittest.DescentUnittestPlugin;
import descent.internal.unittest.flute.FluteApplicationInstance;
import descent.internal.unittest.flute.FluteTestResult;
import descent.internal.unittest.launcher.TestSpecification;
import descent.unittest.ITestRunListener;

/**
 * Interfaces with the FluteApplicationInstance to translate Flute-happy
 * data to data readable by the outside world.
 */
public class RemoteTestRunnerClient implements Runnable
{
	private final List<TestSpecification> tests;
	private final List<ITestRunListener> listeners;
	private final int port;
	private boolean stopped = true;
	private long startTime;
	
	private FluteApplicationInstance app;
	
	public RemoteTestRunnerClient(int $port, List<TestSpecification> $tests,
			List<ITestRunListener> $listeners)
	{
		port = $port;
		tests = $tests;
		listeners = $listeners;
	}
	
	public synchronized boolean isRunning()
	{
		return app != null;
	}
	
	public void init()
	{		
		if(isRunning())
			return;
		
		try
		{
			synchronized(this)
			{
				app = new FluteApplicationInstance(port);
			}
			app.init();
		}
		catch(IOException e)
		{
		}
	}
	
	public void run()
	{
		if(!stopped || !isRunning())
			return;
		
		synchronized(this)
		{
			stopped = false;
		}
		
		startTime = System.currentTimeMillis();
		notifyTestRunStarted(tests.size());
		
		try
		{
			for(TestSpecification test : tests)
			{
				if(stopped)
					break;
				
				runTest(test, false);
			}
			
			long elapsedTime = System.currentTimeMillis() - startTime;
			
			boolean hasStopped;
			synchronized(this)
			{
				hasStopped = stopped;
			}
			
			if(hasStopped)
				notifyTestRunStopped(elapsedTime);
			else
				notifyTestRunEnded(elapsedTime);
			
			synchronized(this)
			{
				stopped = true;
			}
		}
		catch(IOException e)
		{
			DescentUnittestPlugin.log(e);
			terminate();
		}
	}
	
	public synchronized void stopTest()
	{
		stopped = true;
	}
	
	public void terminate()
	{
		if(isRunning())
		{
			try
			{
				app.terminate();
				notifyTestRunTerminated();
			}
			catch(IOException e)
			{
			}
			
			synchronized(this)
			{
				app = null;
			}
		}
	}
	
	public void rerunTest(TestSpecification test)
	{
		if(isRunning() && stopped)
		{
			synchronized(this)
			{
				stopped = false;
			}
			
			try
			{
				runTest(test, true);
			}
			catch(IOException e)
			{
				DescentUnittestPlugin.log(e);
				terminate();
			}
			
			synchronized(this)
			{
				stopped = true;
			}
		}
	}
	
	public void finalize()
	{
		if(isRunning())
		{
			try
			{
				app.terminate();
			}
			catch(IOException e)
			{
			}
		}
	}
	
	private void runTest(TestSpecification test, boolean rerun) throws IOException
	{
		if(!rerun)
			notifyTestStarted(test.getId(), test.getName());
		
		FluteTestResult result = app.runTest(test.getId());
		System.out.println("-------------");
		System.out.println(result);
		System.out.println("-------------");
		
		int statusCode;
		switch(result.getResultType())
		{
			case PASSED:
				statusCode = ITestRunListener.STATUS_OK;
				break;
			case FAILED:
				statusCode = ITestRunListener.STATUS_FAILURE;
				break;
			case ERROR:
				statusCode = ITestRunListener.STATUS_ERROR;
				break;
			default:
				throw new IllegalStateException();
		}
		
		String trace = null;
		if(statusCode != ITestRunListener.STATUS_OK)
		{
			// TODO create trace string
			trace  = "";
		}
		
		if(!rerun)
		{
			if(statusCode == ITestRunListener.STATUS_OK)
				notifyTestEnded(test.getId(), test.getName());
			else
				notifyTestFailed(statusCode, test.getId(), test.getName(), trace);
		}
		else
		{
			notifyTestReran(test.getId(), test.getName(), statusCode, trace);
		}
	}
	
	//--------------------------------------------------------------------------
	// Notification framework
	private static abstract class ListenerSafeRunnable
		implements ISafeRunnable
	{
		public void handleException(Throwable exception)
		{
			DescentUnittestPlugin.log(exception);
		}
	}
	
	private void notifyTestRunStopped(final long elapsedTime)
	{
		for(final ITestRunListener listener : listeners)
		{
			SafeRunner.run(new ListenerSafeRunnable()
			{
				public void run()
				{
					listener.testRunStopped(elapsedTime);
				}
			});
		}
	}
	
	private void notifyTestRunEnded(final long elapsedTime)
	{
		for(final ITestRunListener listener : listeners)
		{
			SafeRunner.run(new ListenerSafeRunnable()
			{
				public void run()
				{
					listener.testRunEnded(elapsedTime);
				}
			});
		}
	}
	
	private void notifyTestRunTerminated()
	{
		if (DescentUnittestPlugin.isStopped())
			return;
		
		for(final ITestRunListener listener : listeners)
		{
			SafeRunner.run(new ListenerSafeRunnable()
			{
				public void run()
				{
					listener.testRunTerminated();
				}
			});
		}
	}
	
	private void notifyTestRunStarted(final int testCount)
	{
		for(final ITestRunListener listener : listeners)
		{
			SafeRunner.run(new ListenerSafeRunnable()
			{
				public void run()
				{
					listener.testRunStarted(testCount);
				}
			});
		}
	}
	
	private void notifyTestStarted(final String testId, final String testName)
	{
		for(final ITestRunListener listener : listeners)
		{
			SafeRunner.run(new ListenerSafeRunnable()
			{
				public void run()
				{
					listener.testStarted(testId, testName);
				}
			});
		}
	}
	
	private void notifyTestEnded(final String testId, final String testName)
	{
		for(final ITestRunListener listener : listeners)
		{
			SafeRunner.run(new ListenerSafeRunnable()
			{
				public void run()
				{
					listener.testEnded(testId, testName);
				}
			});
		}
	}
	
	private void notifyTestFailed(final int status, final String testId,
			final String testName, final String trace)
	{
		for(final ITestRunListener listener : listeners)
		{
			SafeRunner.run(new ListenerSafeRunnable()
			{
				public void run()
				{
					listener.testFailed(status, testId, testName, trace);
				}
			});
		}
	}
	
	private void notifyTestReran(final String testId, final String testName,
			final int status, final String trace)
	{
		for(final ITestRunListener listener : listeners)
		{
			SafeRunner.run(new ListenerSafeRunnable()
			{
				public void run()
				{
					listener.testReran(testId, testName, status, trace);
				}
			});
		}
	}
	
	//--------------------------------------------------------------------------
	// Note: this is just for ad-hoc internal testing & should be removed
	// from the final version
	public static void main(String[] args) throws Exception
	{
		List<TestSpecification> tests = new ArrayList<TestSpecification>();
		tests.add(new TestSpecification("sample.module1.0", "test1", null));
		tests.add(new TestSpecification("sample.module1.1", "test2", null));
		tests.add(new TestSpecification("sample.module1.2", "test3", null));
		tests.add(new TestSpecification("sample.module1.Bar.0", "test4", null));
		
		List<ITestRunListener> listeners = 
			new ArrayList<ITestRunListener>(1);
		listeners.add(new ITestRunListener()
		{
			public void testEnded(String testId, String testName )
			{
				//System.out.println("testEnded(" + testId + ", " + testName + ");");
			}

			public void testFailed(int status, String testId, String testName,  String trace)
			{
				//System.out.println("testFailed(" + status + ", " + testId
				//		+ ", " + testName + ", " + trace + ");");
			}

			public void testReran(String testId, String testName, int status, String trace)
			{
				//System.out.println("testReran(" + testId + ", " + testName + ", " +
				//		+ status + ", " + trace + ");");
			}

			public void testRunEnded(long elapsedTime)
			{
				//System.out.println("testRunEnded();");
			}

			public void testRunStarted(int testCount)
			{
				//System.out.println("testRunStarted();");
			}

			public void testRunStopped(long elapsedTime)
			{
				//System.out.println("testRunStopped();");
			}

			public void testRunTerminated()
			{
				//System.out.println("testRunTerminated();");
			}

			public void testStarted(String testId, String testName)
			{
				//System.out.println("testStarted(" + testId + ", " + testName + ");");
			}
		});
		
		Process proc = (new ProcessBuilder(new String[] {
				"C:/Users/xycos/workspace/descent.unittest/testdata/" +
					"src/test.exe"
			})).start();
		
		try
		{
			RemoteTestRunnerClient client = new RemoteTestRunnerClient(
					30587, tests, listeners);
			client.init();
			client.run();
			client.terminate();
		}
		finally
		{
			Thread.sleep(1000); // Let it finish up whatever it's doing
			try
			{
				System.out.println("App finished with exit code: " +
						proc.exitValue());
			}
			catch(IllegalThreadStateException e)
			{
				System.out.println("Had to manually kill the process");
				proc.destroy();
			}
		}
	}
}
