package descent.internal.launching.debuild;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import descent.internal.launching.LaunchingPlugin;
import descent.launching.compiler.IExecutableCommand;
import descent.launching.compiler.IResponseInterpreter;

/**
 * Starts & monitors the execution of a process (compiler or linker) and passes
 * information along to a response interpreter.
 *
 * @author Robert Fraser
 */
public class ExecutionMonitor implements Runnable
{	
	/**
	 * Non-blocking mechanism for reading stream input line-by-line
	 * and forwarding calls to the interpret() method
	 * 
	 * @author Robert Fraser
	 */
	private class LineByLineReader
	{
		private Thread thread;
		private BufferedReader reader;
		private long lastSleep;
		private final boolean isError;

		LineByLineReader(InputStream stream, boolean isError)
		{
			reader = new BufferedReader(new InputStreamReader(stream));
			this.isError = isError;
		}

		void start()
		{
			if (null == thread)
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
					{
						if(isError)
							interpreter.interpretError(text);
						else
							interpreter.interpret(text);
					}
				}
				catch (IOException e)
				{
					// This generally just means the end of the stream, so break
					break;
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
				LaunchingPlugin.log(e);
			}
		}
	}
	
	private final String command;
	private final IResponseInterpreter interpreter;
	private final String[] environment;
	private final String workingDir;
	
	private boolean finished = false;
	
	public ExecutionMonitor(IExecutableCommand command,
			IResponseInterpreter interpreter, String[] environment,
			String workingDir)
	{
		if(!command.isValid())
			throw new IllegalArgumentException("Invalid command");
		
		this.command = command.getCommand();
		this.interpreter = interpreter;
		this.environment = environment;
		this.workingDir = workingDir;
	}
	
	public void run()
	{	
		if(finished)
			return;
		Process proc = null;
		
		try
		{
			if(DebuildBuilder.DEBUG)
				System.out.println(command);
			
			proc = Runtime.getRuntime().exec(command,
					environment, new File(workingDir));
			LineByLineReader stdoutReader = 
				new LineByLineReader(proc.getInputStream(), false);
			LineByLineReader stderrReader = 
				new LineByLineReader(proc.getErrorStream(), true);
			stdoutReader.start();
			stderrReader.start();
			
			proc.waitFor();  // PERHAPS timeout in case of hangs?
			finished = true;
		}
		catch(Exception e)
		{
			// Kill the process if need be
			if(null != proc)
			{
				try
				{
					proc.destroy();
				}
				catch(Exception ex)
				{
					// Do nothing
				}
			}
			
			LaunchingPlugin.log(e);
		}
	}
	
	public boolean finished()
	{
		return finished;
	}
}
