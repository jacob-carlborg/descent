package descent.building.compiler;

/**
 * Class used for callbacks monitoring the execution of a program.
 * 
 * <b>This class should be implemented by clients providing a compiler interface.</b>
 * 
 * @author Robert Fraser
 */
public interface IExecutionMonitor
{
    /**
     * Called for every line of output written to Stdout by a process.
     * 
     * @param line
     */
    public void interpret(String line);
    
    /**
     * Called for every line of output written to Stderr by a process.
     * 
     * @param line
     */
    public void interpretError(String line);
}
