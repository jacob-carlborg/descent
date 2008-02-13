package descent.internal.core.builder.debuild;

import java.io.File;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;

import descent.core.builder.IExecutableCommand;
import descent.core.builder.IResponseInterpreter;

/**
 * Starts & monitors the execution of a process (compiler or linker) and passes
 * information along to a response interpreter.
 *
 * @author Robert Fraser
 */
public class ExecutionMonitor implements Runnable
{	
	private class ResponseStreamListener implements IStreamListener
	{
		private final StringBuilder fStreamBuffer;
		private boolean fIsError;
		
		public ResponseStreamListener(boolean isError)
		{
			fIsError = isError;
			fStreamBuffer = new StringBuilder();
		}
		
		public void streamAppended(String text, IStreamMonitor monitor)
		{
			fStreamBuffer.append(text);
			readLines();
		}
		
		public void flush()
		{
			readLines();
			
			String text = fStreamBuffer.toString();
			interpreter.interpretError(text);
			fStreamBuffer.setLength(0);
		}
		
		private void readLines()
		{
			int indexOfLine = fStreamBuffer.indexOf("\n"); //$NON-NLS-1$
			if (indexOfLine == -1) return;
			
			int lastIndexOfLine = 0;
			
			while(indexOfLine != -1)
			{
				String line = fStreamBuffer.substring(lastIndexOfLine, indexOfLine);
				if (fIsError)
				{
					interpreter.interpretError(line);
				}
				else
				{
					interpreter.interpret(line);
				}
				
				lastIndexOfLine = indexOfLine + 1;
				indexOfLine = fStreamBuffer.indexOf("\n", lastIndexOfLine); //$NON-NLS-1$
			}
			
			fStreamBuffer.delete(0, lastIndexOfLine);
		}
	}
	
	private final IExecutableCommand command;
	private final IResponseInterpreter interpreter;
	private final String[] environment;
	private final String workingDir;
	
	private IProcess process;
	private boolean finished = false;
	
	public ExecutionMonitor(IExecutableCommand command,
			IResponseInterpreter interpreter, String[] environment,
			String workingDir)
	{
		if(!command.isValid())
			throw new IllegalArgumentException("Invalid command");
		
		this.command = command;
		this.interpreter = interpreter;
		this.environment = environment;
		this.workingDir = workingDir;
	}
	
	public void run()
	{	
		if(finished || null != process)
			return;
		Process proc = null;
		
		try
		{
			proc = Runtime.getRuntime().exec(command.getCommand(),
					environment, new File(workingDir));
			process = DebugPlugin.newProcess(null, proc, null);
			process.getStreamsProxy().getOutputStreamMonitor().addListener(
					new ResponseStreamListener(false));
			process.getStreamsProxy().getErrorStreamMonitor().addListener(
					new ResponseStreamListener(true));
			
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
			
			// TODO log/handle
			e.printStackTrace();
		}
	}
	
	public boolean finished()
	{
		return finished;
	}
}
