/*******************************************************************************
 * Copyright (c) 2007 Robert Fraser
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package descent.internal.unittest.flute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;

import descent.internal.unittest.DescentUnittestPlugin;

/**
 * Interfaces with the streams of a fluted application to do cool stuff
 * like run a command & get the result of it.
 * 
 * @author Robert Fraser
 */
public class FluteApplicationInstance
{
	// Yes, these should all have different protections :-P
	public static final String FLUTE_VERSION = "flute 0.1";
	static final String AWAITING_INPUT = "(flute)";
	private static final String LOCALHOST  = "127.0.0.1";
	
	private long fTimeout = 10000;
	private final Object fWaitLock = new Object();
	private volatile boolean fWaitLockUsed;
	
	private IState fState = new StartingUp(this);
	private final IState fWaitingState = new Waiting(this);
	
	private final int fPort;
	
	private Socket fConn;
	private OutputStream fOut;
	private LineByLineReader fIn;
	
	/**
	 * Creates a new FluteApplicationInstance to connect to the specified
	 * port.
	 * 
	 * @param port the port to connect to
	 */
	public FluteApplicationInstance(int port)
	{
		// Just save the port number, init() will actually create the
		// connection
		this.fPort = port;
	}
	
	/**
	 * Sets the timeout for commands. Particularly important when running
	 * tests that could take a while.
	 * 
	 * @param timeout the timeout, in milliseconds
	 */
	public void setTimeout(long timeout)
	{
		fTimeout = timeout;
	}
	
	/**
	 * Initializes the flute application. Should be called only once
	 * and before any calls to other methods.
	 */
	public void init() throws IOException
	{
		assert(fState instanceof StartingUp);
		assert(null == fConn);
		try
		{	
			beforeWaitStateReturn();
			
			fConn = new Socket(LOCALHOST, fPort);
			fIn = new LineByLineReader(fConn.getInputStream());
			fOut = fConn.getOutputStream();
			fIn.startMonitoring();
			
			waitStateReturn();
			assert(((StartingUp) fState).hasCorrectVersion);
		}
		finally
		{
			setState(fWaitingState);
		}
	}
	
	/**
	 * Runs a single test and returns the result, or null if the test
	 * timed out.
	 * 
	 * @param signature the signature of the test
	 * @return          the result of running the test or null if the
	 *                  test timed out
	 */
	public FluteTestResult runTest(String signature) throws IOException
	{
		try
		{
			setState(new RunningOneTest(this));
			
			beforeWaitStateReturn();
			write("r " + signature + "\r\n"); //$NON-NLS-1$
			waitStateReturn();
			
			return ((RunningOneTest) fState).getResult();	
		}
		finally
		{
			setState(fWaitingState);
		}
	}
	
	/**
	 * Terminates the application. No other calls to this instance should
	 * be made after this one.
	 */
	public void terminate() throws IOException
	{
		try
		{
			write("x\r\n");
		}
		finally
		{
			fConn.close();
		}
	}
	
	private void beforeWaitStateReturn() {
		fWaitLockUsed = false;
	}
	
	private void waitStateReturn() {
		try {
			synchronized (fWaitLock) {
				if (!fWaitLockUsed) {
					fWaitLock.wait(fTimeout);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	void notifyStateReturn() {
		fWaitLockUsed = true;
		synchronized (fWaitLock) {
			fWaitLock.notify();
		}		
	}
	
	private void setState(IState state)
	{
		fState = state;
	}
	
	void interpret(String text) throws IOException
	{
		fState.interpret(text.trim());
	}
	
	private void write(String text) throws IOException
	{
		fOut.write(text.getBytes());
	}
	
	/**
	 * Non-blocking mechanism for reading socket input line-by-line
	 * and forwarding calls to the interpret() method
	 * 
	 * @author Robert Fraser
	 */
	private class LineByLineReader
	{		
		private Thread fThread;
		private BufferedReader fReader;
		private long lastSleep;
		
		LineByLineReader(InputStream stream)
		{
			try
			{
				fReader = new BufferedReader(new InputStreamReader(stream,
						"UTF-8"));
			}
			catch(UnsupportedEncodingException e)
			{
				DescentUnittestPlugin.log(e);
			}
		}
		
		void startMonitoring()
		{
			if (fThread == null)
			{
				fThread= new Thread(new Runnable()
				{
					public void run()
					{
						read();
					}
				}); 
				
	            fThread.setDaemon(true);
	            fThread.setPriority(Thread.MIN_PRIORITY);
				fThread.start();
			}
		}
		
		void read()
		{
			lastSleep = System.currentTimeMillis();
			long currentTime = lastSleep;
			while (true)
			{
				try
				{
					String text = fReader.readLine();
					if (null == text)
						break;
					else
						interpret(text);
				}
				catch(SocketException e)
				{
					break;
				}
				catch (IOException e)
				{
					DescentUnittestPlugin.log(e);
					return;
				}

				// Give up some CPU time to maintain UI responsiveness
				currentTime = System.currentTimeMillis();
				if (currentTime - lastSleep > 1000)
				{
					lastSleep = currentTime;
					try
					{
						Thread.sleep(1);
					}
					catch (InterruptedException e)
					{
						// Do nothing
					}
				}
			}
			
			try
			{
				fReader.close();
			}
			catch (IOException e)
			{
				DescentUnittestPlugin.log(e);
			}
		}
	}
	
	// Note: this is just for ad-hoc internal testing & should be removed
	// from the final version
	public static void main(String[] args) throws Exception
	{
		Process proc = (new ProcessBuilder(new String[] {
				"C:/Users/xycos/workspace/descent.unittest/testdata/" +
					"src/test.exe"
			})).start();
		FluteApplicationInstance instance = 
			new FluteApplicationInstance(30587);
		
		System.out.println("Starting test...");
		instance.init();
		
		System.out.print("Running sample.module1.0... ");
		FluteTestResult res = instance.runTest("sample.module1.0");
		System.out.println(res.getResultType());
		
		System.out.print("Running sample.module1.1... ");
		res = instance.runTest("sample.module1.1");
		System.out.println(res.getResultType());
		
		System.out.print("Running sample.module1.2... ");
		res = instance.runTest("sample.module1.2");
		System.out.println(res.getResultType());
		
		System.out.print("Running sample.module1.Bar.0... ");
		res = instance.runTest("sample.module1.Bar.0");
		System.out.println(res.getResultType());
		
		System.out.println("Shutting down...");
		instance.terminate();
		
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
