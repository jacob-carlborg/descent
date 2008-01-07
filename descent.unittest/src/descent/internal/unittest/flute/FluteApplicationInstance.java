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
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;

/**
 * Interfaces with the streams of a fluted application to do cool stuff
 * like run a command & get the result of it.
 * 
 * @author Robert Fraser
 */
public class FluteApplicationInstance
{
	/**
	 * Non-blocking mechanism for reading socket input line-by-line
	 * and forwarding calls to the interpret() method
	 * 
	 * @author Robert Fraser
	 */
	private static class LineByLineReader
	{
		// TODO make this class non-static once tested, so interpret()
		// can be called.
		
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
				//TODO DescentUnittestPlugin.log(e);
				System.out.println(e);
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
		
		private void read()
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
						// TODO interpret(text);
						System.out.println("APPENDED: " + text.trim());
				}
				catch (IOException e)
				{
					//TODO DescentUnittestPlugin.log(e);
					System.out.println(e);
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
				//TODO DescentUnittestPlugin.log(e);
				System.out.println(e);
			}
		}
	}
	
	public static final String FLUTE_VERSION = "flute 0.1";
	
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
	public void init()
	{
		assert(fState instanceof StartingUp);
		try
		{
			beforeWaitStateReturn();
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
			fOut.write("r " + signature + "\r\n"); //$NON-NLS-1$
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
		fOut.write("x\r\n");
	}
	
	static final String AWAITING_INPUT = "(flute)";
	
	private long fTimeout = 10000;
	private final Object fWaitLock = new Object();
	private volatile boolean fWaitLockUsed;
	
	private IState fState;
	private final IState fWaitingState = new Waiting(this);
	
	private final int port;
	
	private Writer fOut;
	private Reader fIn;
	
	public FluteApplicationInstance(int port)
	{
		// Just save the port number, init() will actually create the
		// connection
		this.port = port;
	}
	
	void beforeWaitStateReturn() {
		fWaitLockUsed = false;
	}
	
	void waitStateReturn() {
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
	
	void setState(IState state)
	{
		fState = state;
	}
	
	void interpret(String text) throws IOException
	{
		fState.interpret(text.trim());
	}
	
	// Note: this is just for ad-hoc internal testing & should be removed
	// from the final version
	public static void main(String[] args) throws Exception
	{
		/*
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
		*/
		
		System.out.println("Starting test...");
		
		Process proc = (new ProcessBuilder(new String[] {
				"C:/Users/xycos/workspace/descent.unittest/testdata/" +
					"src/test.exe"
			})).start();
		Socket sock = new Socket("127.0.0.1", 30587);
		InputStream in = sock.getInputStream();
		OutputStream out = sock.getOutputStream();
		
		LineByLineReader reader = new LineByLineReader(in);
		reader.startMonitoring();
		
		Writer writer = new OutputStreamWriter(out, "UTF-8");
		
		Thread.sleep(500);
		synchronized(System.out)
		{
			System.out.println("WRITING: r sample.module1.0");
			writer.write("r sample.module1.0\r\n");
		}
		
		Thread.sleep(500);
		synchronized(System.out)
		{
			System.out.println("WRITING: x");
			writer.write("x\r\n");
		}
		
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
