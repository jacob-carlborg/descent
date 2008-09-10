package descent.building.compiler;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * DOCS moar
 * 
 * @author Robert Fraser
 */
public interface IExecutionMonitor
{
    /**
     * Adds the given command to the execution queue
     * 
     * @param cmd         the system command to execute. Must not be null
     * @param interpreter the response interpreter. If null, responses will be ignored.
     * @param onComplete  the runnable to be called when the task finishes. If null, nothing will be done on task completion.
     * @param workingDir  the working directory to execute the command. If null, the output directory of the project (where
     *                    object files go).
     * @param env         extensions/replacements for environment variables. If null, no extensions will be used.
     * @return            a future for the response code of the program.
     */
    public Future<Integer> exec(String cmd, IResponseInterpreter interpreter, IExecutionCallback onComplete, String workingDir, String[] env);
    
    /**
     * Same as {@link #exec(String, IResponseInterpreter, IExecutionCallback, String, String[])} called as
     * <code>execSync(cmd, interpreter, onComplete, null, null);
     */
    public Future<Integer> exec(String cmd, IResponseInterpreter interpreter, IExecutionCallback onComplete);
    
    /**
     * Waits for all previously submitted tasks to complete. If the user cancels the progress
     * monitor, will call {@link #shutdownNow()} at some point (it may take up to half a second --
     * or more, depending on thread scheduling -- for the cancel request to be recorded). Any tasks
     * added after this call will still be executed (and additional tasks can still be added after
     * this call), however this call will return before those tasks have been completed.
     * 
     * @param terminator the progress monitor to use to check termination or null if no progress 
     *                   monitor should be checked
     * @return           true if the tasks completed successfully, false if the user canceled it.
     */
    public boolean syncPreviousTasks(IProgressMonitor terminator);
    
    /**
     * Attempts to stop any running processes. Will not directly cancel any tasks, however any new tasks that
     * come up for execution will stop immediately. This should be used only if the user cancels the whole
     * build. No new tasks should be submitted for execution after this point.
     * 
     * It may take some time before any processes are completely stopped. Remember that if a process is using 100% 
     * CPU it may take even longer for other processes to get CPU slots.
     */
    public void shutdownNow();
}
