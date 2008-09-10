package descent.internal.building.debuild;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;

import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;

import descent.internal.building.BuildingPlugin;
import descent.building.compiler.IExecutionCallback;
import descent.building.compiler.IExecutionMonitor;
import descent.building.compiler.IResponseInterpreter;

/**
 * Starts & monitors the execution of a process (compiler or linker) and passes
 * information along to a response interpreter.
 *
 * @author Robert Fraser
 */
/* package */ class ExecutionMonitor implements IExecutionMonitor
{
    private static final IResponseInterpreter NULL_RESPONSE_INTERPRETER =
        new IResponseInterpreter() {
            public void interpret(String line) { }
            public void interpretError(String line) { }
        };
    private static final IExecutionCallback NULL_CALLBACK = 
        new IExecutionCallback() { public void taskCompleted(int result) { } };
    private static final Callable<Boolean> NULL_CALLABLE =
        new Callable<Boolean>() { public Boolean call() { return true; } };
    
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
    
	private final class ExecutionTask implements Callable<Integer>
	{
	    /**
	     * Listens to stream events and passes them on to the appropriate interpreter
	     * 
	     * @author Robert Fraser
	     */
	    private final class StreamListner implements IStreamListener
        {
            private final boolean isError;
            
            public StreamListner(boolean isError)
            {
                this.isError = isError;
            }
            
            public void streamAppended(String text, IStreamMonitor monitor)
            {
                int start = 0;
                int len = text.length();
                for(int i = 0; i < len; i++)
                {
                    char c = text.charAt(i);
                    if (c == '\n')
                    {
                        int end = i;
                        if(end > 0 && (text.charAt(end - 1) == '\r'))
                            --end;
                        String line = text.substring(start, end);
                        if(isError)
                            interpreter.interpretError(line);
                        else
                            interpreter.interpret(line);
                        start = i + 1;
                    }
                }
            }
        }
	    
	    private final String command;
	    private final IResponseInterpreter interpreter;
	    private final String[] environment;
	    private final String workingDir;
	    private final IExecutionCallback onComplete;
	    
	    public ExecutionTask(String command,
	            IResponseInterpreter interpreter,
	            String[] environment,
	            String workingDir,
	            IExecutionCallback onComplete)
	    {   
	        this.command = command;
	        this.environment = environment;
	        this.workingDir = null ==  workingDir ?  defaultWorkingDir : workingDir;
	        this.onComplete = null == onComplete ? NULL_CALLBACK : onComplete;
	        
	        interpreter = null == interpreter ? NULL_RESPONSE_INTERPRETER :  interpreter;
	        interpreter = DebuildBuilder.DEBUG ? new DebugResponseInterpreter(interpreter) : interpreter;
	        this.interpreter = interpreter;
	    }
	    
	    public Integer call()
	    {
	        Process proc = null;
	        try
	        {
	            if(DebuildBuilder.DEBUG)
	                System.out.println("EXC => " + command);
	            
	            proc = Runtime.getRuntime().exec(command,
	                    environment, new File(workingDir));
	            IProcess eclipseProc = DebugPlugin.newProcess(launch, proc, command);
	            IStreamsProxy streamsProxy = eclipseProc.getStreamsProxy();
	            streamsProxy.getOutputStreamMonitor().addListener(new StreamListner(false));
	            streamsProxy.getErrorStreamMonitor().addListener(new StreamListner(true));
	            
	            int result;
	            try
	            {
	                result = proc.waitFor();
	            }
                catch(InterruptedException e)
                {
                    proc.destroy();
                    return null;
                }
	            
	            if(DebuildBuilder.DEBUG)
	                System.out.println("TRM => " + result);
	            onComplete.taskCompleted(result);
	            return result;
	        }
	        catch(Exception e)
	        {
	            // Kill the process if need be
	            if(null != proc)
	                proc.destroy();
	            BuildingPlugin.log(e);
	            return null;
	        }
	    }
	}
	
	private final ExecutorService threadPool;
	private final ILaunch launch;
	private final String defaultWorkingDir;
	
	public ExecutionMonitor(ILaunch launch, String defaultWorkingDir, boolean useThreadPooling)
	{
	    int numThreads = (DebuildBuilder.DEBUG || !useThreadPooling) ? 1 :
	        Runtime.getRuntime().availableProcessors();
	    this.threadPool = Executors.newFixedThreadPool(numThreads);
	    this.launch = launch;
	    this.defaultWorkingDir = defaultWorkingDir;
	}
	
	/* (non-Javadoc)
	 * @see descent.building.compiler.IExecutionMonitor#exec(java.lang.String, descent.building.compiler.IResponseInterpreter, descent.building.compiler.IExecutionCallback)
	 */
	public Future<Integer> exec(String cmd, IResponseInterpreter interpreter,
            IExecutionCallback onComplete)
    {
        return exec(cmd, interpreter, onComplete, defaultWorkingDir, null);
    }
	
    /* (non-Javadoc)
	 * @see descent.building.compiler.IExecutionMonitor#exec(java.lang.String, descent.building.compiler.IResponseInterpreter, descent.building.compiler.IExecutionCallback, java.lang.String, java.lang.String[])
	 */
	public Future<Integer> exec(String command,
            IResponseInterpreter interpreter,
            IExecutionCallback onComplete,
            String workingDir,
            String[] environment)
    {
	    return threadPool.submit(new ExecutionTask(command, interpreter, environment, workingDir, onComplete));
    }
	
	/* (non-Javadoc)
	 * @see descent.building.compiler.IExecutionMonitor#syncPreviousTasks(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean syncPreviousTasks(IProgressMonitor terminator)
	{
	    // Come with me... if you want to live.
	    if(null == terminator)
	        terminator = new NullProgressMonitor();
	    Future<Boolean> task = threadPool.submit(NULL_CALLABLE);
	    while(true)
	    {
	        if(terminator.isCanceled())
	        {
	            shutdownNow();
	            
	            // Wait one second in case some processes aren't killed properly
	            try
	            {
	                threadPool.awaitTermination(1, TimeUnit.SECONDS);
	            }
	            catch(Exception e) { }
	            return false;
	        }
	        
	        try
	        {
	            task.get(500, TimeUnit.MILLISECONDS);
	            return true;
	        }
	        catch(Exception e)
	        {
	            // A timeout exception indicating that the request timed out... go through
	            // the loop again
	        }
	    }
	}
	
	/* (non-Javadoc)
	 * @see descent.building.compiler.IExecutionMonitor#shutdownNow()
	 */
	public void shutdownNow()
	{
	    threadPool.shutdownNow();
	}
}
