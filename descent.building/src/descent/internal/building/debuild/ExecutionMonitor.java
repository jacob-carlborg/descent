package descent.internal.building.debuild;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Deque;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.eclipse.debug.core.ILaunch;

import descent.internal.building.BuildingPlugin;
import descent.building.compiler.IResponseInterpreter;

/**
 * Starts & monitors the execution of a process (compiler or linker) and passes
 * information along to a response interpreter.
 *
 * @author Robert Fraser
 */
/* package */ class ExecutionMonitor
{
    /**
     * Response interpreter that simply wraps the streams logging info to Eclipse stdout.
     * This is useful for debugging purposes (obviously).
     * 
     * @author Robert Fraser
     */
    private static class DebugResponseInterpreter implements IResponseInterpreter
    {
        private IResponseInterpreter interpreter;
        
        public DebugResponseInterpreter(IResponseInterpreter interpreter)
        {
            this.interpreter = interpreter;
        }
        
        public void interpret(String line)
        {
            System.out.println("OUT => " + line);
            interpreter.interpret(line);
        }

        public void interpretError(String line)
        {
            System.out.println("ERR => " + line);
            interpreter.interpretError(line);
        }
    }
    
    /**
     * Response interpreter that doesn't do anything.
     * 
     * @author Robert Fraser
     */
    private static class NullResponseInterpreter implements IResponseInterpreter
    {
        public void interpret(String line) {  }
        public void interpretError(String line) {  }
    }
    
	private final class ExecutionTask implements Callable<Integer>
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
	                BuildingPlugin.log(e);
	            }
	        }
	    }
	    
	    private final String command;
	    private final IResponseInterpreter interpreter;
	    private final String[] environment;
	    private final String workingDir;
	    
	    private boolean finished = false;
	    
	    public ExecutionTask(String command,
	            IResponseInterpreter interpreter,
	            String[] environment,
	            String workingDir)
	    {   
	        this.command = command;
	        this.environment = environment;
	        this.workingDir = workingDir;
	        
	        if(null == interpreter)
	            interpreter = new NullResponseInterpreter();
	        this.interpreter = DebuildBuilder.DEBUG ? new DebugResponseInterpreter(interpreter) : interpreter;
	    }
	    
	    public Integer call()
	    {
	        if(finished)
	            throw new IllegalStateException();
	        Process proc = null;
	        
	        try
	        {
	            if(DebuildBuilder.DEBUG)
	                System.out.println("EXC => " + command);
	            
	            proc = Runtime.getRuntime().exec(command,
	                    environment, new File(workingDir));
	            // TODO add the process to the launch
	            LineByLineReader stdoutReader = 
	                new LineByLineReader(proc.getInputStream(), false);
	            LineByLineReader stderrReader = 
	                new LineByLineReader(proc.getErrorStream(), true);
	            stdoutReader.start();
	            stderrReader.start();
	            
	            int result = proc.waitFor();  // PERHAPS timeout in case of hangs?
	            finished = true;
	            return result;
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
	            
	            BuildingPlugin.log(e);
	            return null;
	        }
	    }
	    
	    public boolean finished()
	    {
	        return finished;
	    }
	}
	
	private final ExecutorService threadPool;
	private final ILaunch launch;
	
	public ExecutionMonitor(ILaunch launch)
	{
	    int numThreads = DebuildBuilder.DEBUG ? 1 : Runtime.getRuntime().availableProcessors();
	    this.threadPool = Executors.newFixedThreadPool(numThreads);
	    this.launch = launch;
	}
	
	public Future<Integer> addTask(String command,
            IResponseInterpreter interpreter,
            String[] environment,
            String workingDir)
    {
	    return threadPool.submit(new ExecutionTask(command, interpreter, environment, workingDir));
    }
	
	public void waitFor()
	{
	    try
	    {
	        threadPool.awaitTermination(1, TimeUnit.DAYS);
	    }
	    catch(Exception e)
	    {
	        throw e instanceof RuntimeException ? ((RuntimeException) e) : new RuntimeException(e);
	    }
	}
}
