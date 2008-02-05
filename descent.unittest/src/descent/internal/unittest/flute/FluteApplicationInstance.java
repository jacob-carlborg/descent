/*******************************************************************************
 * Copyright (c) 2007 Robert Fraser
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package descent.internal.unittest.flute;

import java.io.BufferedReader;
import java.io.Closeable;
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
	
	private SocketConnection fConn;
	
	private long fTimeout = 10000;
	private final Object fWaitLock = new Object();
	private volatile boolean fWaitLockUsed;
	
	private IState fState = new StartingUp(this);
	private final IState fWaitingState = new Waiting(this);
	
	private final int fPort;
	
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
		
		beforeWaitStateReturn();
		
		// Make the connection (interpretation will begin automatically)
		fConn = new SocketConnection(fPort);
		
		waitStateReturn();
		assert(((StartingUp) fState).hasCorrectVersion);
		setState(fWaitingState);
	}
	
	public boolean isConnected()
	{
		return !(fState instanceof StartingUp);
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
			fConn.write("r " + signature + "\r\n"); //$NON-NLS-1$
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
			fConn.write("x\r\n");
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
	
	private class SocketConnection implements Closeable
	{
		private final Socket socket;
		private final OutputStream out;
		private final LineByLineReader in;

		private static final String LOCALHOST = "127.0.0.1";

		SocketConnection(int port)
				throws IOException
		{
			socket = new Socket(LOCALHOST, port);
			in = new LineByLineReader(socket.getInputStream());
			out = socket.getOutputStream();
			in.start();
		}

		void write(String text) throws IOException
		{
			out.write(text.getBytes());
		}

		/**
		 * Closes the socket (the other stuff will close on its own)
		 * 
		 * @throws IOException
		 */
		public void close() throws IOException
		{
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
			}
		}

		/**
		 * Non-blocking mechanism for reading socket input line-by-line
		 * and forwarding calls to the interpret() method
		 * 
		 * @author Robert Fraser
		 */
		private class LineByLineReader
		{
			private Thread thread;
			private BufferedReader reader;
			private long lastSleep;

			LineByLineReader(InputStream stream)
			{
				try
				{
					reader = new BufferedReader(new InputStreamReader(stream,
							"UTF-8"));
				}
				catch (UnsupportedEncodingException e)
				{
					reader = new BufferedReader(new InputStreamReader(stream));
				}
			}

			void start()
			{
				if (thread == null)
				{
					thread = new Thread(new Runnable()
					{
						public void run()
						{
							read();
						}
					});

					thread.setDaemon(true);
					thread.setPriority(Thread.MIN_PRIORITY);
					thread.start();
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
						String text = reader.readLine();
						if (null == text)
							break;
						else
							interpret(text);
					}
					catch (SocketException e)
					{
						// If a socket exception is sent, it usually means
						// we're done, since the socket was closed.
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
					reader.close();
				}
				catch (IOException e)
				{
					DescentUnittestPlugin.log(e);
				}
			}
		}
	}
}
