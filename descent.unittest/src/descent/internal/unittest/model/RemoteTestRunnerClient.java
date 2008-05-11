/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package descent.internal.unittest.model;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

import descent.internal.unittest.DescentUnittestPlugin;
import descent.internal.unittest.flute.FluteApplicationInstance;
import descent.internal.unittest.flute.FluteTestResult;
import descent.unittest.IStackTraceElement;
import descent.unittest.ITestResult;
import descent.unittest.ITestRunListener;
import descent.unittest.ITestSpecification;

/**
 * Interfaces with the FluteApplicationInstance to translate Flute-happy
 * data to data readable by the outside world.
 */
public class RemoteTestRunnerClient
{
	private final List<ITestSpecification> tests;
	private final List<ITestRunListener> listeners;
	private boolean stopped = true;
	private long startTime;
	
	private FluteApplicationInstance app;
	
	public RemoteTestRunnerClient(FluteApplicationInstance $app,
	        List<ITestSpecification> $tests,
			List<ITestRunListener> $listeners)
	{
		app = $app;
		tests = $tests;
		listeners = $listeners;
	}
	
	public List<ITestSpecification> getTests()
	{
		return tests;
	}
	
	public synchronized boolean isRunning()
	{
		return app != null;
	}
	
	public synchronized boolean isConnected()
	{
		return app.isConnected();
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
		notifyTestRunStarted(tests);
		
		try
		{
			for(ITestSpecification test : tests)
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
	
	public void rerunTest(ITestSpecification test)
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
		// Never good to leave those processes open...
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
	
	private void runTest(ITestSpecification test, boolean rerun) 
		throws IOException
	{	
		if(!rerun)
			notifyTestStarted(test);
		
		ITestResult result = app.runTest(test.getId());
		
		if(!rerun)
			notifyTestEnded(test, result);
		else
			notifyTestReran(test, result);
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
	
	private void notifyTestRunStarted(final List<ITestSpecification> tests)
	{
		for(final ITestRunListener listener : listeners)
		{
			SafeRunner.run(new ListenerSafeRunnable()
			{
				public void run()
				{
					listener.testRunStarted(tests);
				}
			});
		}
	}
	
	private void notifyTestStarted(final ITestSpecification test)
	{
		for(final ITestRunListener listener : listeners)
		{
			SafeRunner.run(new ListenerSafeRunnable()
			{
				public void run()
				{
					listener.testStarted(test);
				}
			});
		}
	}
	
	private void notifyTestEnded(final ITestSpecification test, 
			final ITestResult result)
	{
		for(final ITestRunListener listener : listeners)
		{
			SafeRunner.run(new ListenerSafeRunnable()
			{
				public void run()
				{
					listener.testEnded(test, result);
				}
			});
		}
	}
	
	private void notifyTestReran(final ITestSpecification test,
			final ITestResult result)
	{
		for(final ITestRunListener listener : listeners)
		{
			SafeRunner.run(new ListenerSafeRunnable()
			{
				public void run()
				{
					listener.testReran(test, result);
				}
			});
		}
	}
}
