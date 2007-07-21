package descent.debug.core.model;

import java.io.IOException;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.IVariable;

import descent.debug.core.IDebuggerRegistry;


/**
 * <p>A console debugger. A debugger communicates with an underlying
 * program that controles the debuggee's execution by sending requests (i.e. step into,
 * step out, add breakpoint, etc.) and by recieving responses (i.e. breakpoint hit, end, etc.).</p>
 * 
 * <p>A debugger shall not worry about synchronizing the requests sent
 * to the underyling program, nor worry about synchronizing the responses.
 * This is handled by the Descent Launching platform.</p>
 * 
 * <p>
 * A debugger extension is defined in <code>plugin.xml</code>.
 * Following is an example definition of a debugger extension.
 * <pre>
 * &lt;extension point="descent.launching.debuggers"&gt;
 *   &lt;debugger 
 *      id="com.example.debuggerIdentifier"
 *      class="com.example.ExampleDebugger"
 *      name="Example Debugger"&gt;
 *   &lt;/debugger&gt;
 * &lt;/extension&gt;
 * </pre>
 * The attributes are specified as follows:
 * <ul>
 * <li><code>id</code> specifies a unique identifier for the debugger - see {@link IDebuggerRegistry}</li>
 * <li><code>class</code> specifies the fully qualified name of the Java class
 *   that implements this interface.</li>
 * <li><code>name</code> the name of the debugger.</li>
 * </ul>
 * </p>
 * 
 * <p>Clients may implement this interface.</p>
 * 
 * @see IDebugElementFactory
 * @see IDebuggerRegistry
 */
public interface IDebugger {
	
	/**
	 * Returns a list of command line arguments that must be passed
	 * to this debugger before starting.
	 * 
	 * @return a list of command line arguments
	 */
	List<String> getDebuggerCommandLineArguments();
	
	/**
	 * <p>Returns a list of command line arguments that are used
	 * to pass the given <i>arguments</i> to the debuggee.<p>
	 * 
	 * <p>The returned list generally contains the same elements
	 * as arguments, but in some cases an argument must be added
	 * before the others (for example "-args"). 
	 * @param arguments the arguments to add to the debuggee
	 * @returna a list of command line arguments
	 */
	List<String> getDebugeeCommandLineArguments(String[] arguments);
	
	/**
	 * <p>Returns the string that is used by this debugger to indicate
	 * that a single communication has ended. This string may be,
	 * for example "->" (in ddbg) or "(gdb)" (in gdb).</p>
	 * @return an end-communication string
	 */
	String getEndCommunicationString();
	
	/**
	 * Setups this debugger with the given arguments.
	 * @param listener a listener to which notify events
	 * @param factory a factory used to create the serveral objects that this debugger must return
	 * @param out an output stream to which send requests
	 * @param timeout the timeout of communication
	 * @param showBaseMembersInSameLevel wether to show the base members in the same level
	 * in <code>getVariables</code> and <code>evaluateExpression</code>
	 * 
	 * @see IDebuggerListener
	 * @see IDebugElementFactory
	 */
	void initialize(IDebuggerListener listener, IDebugElementFactory factory, IStreamsProxy out, int timeout, boolean showBaseMembersInSameLevel);
	
	/**
	 * <p>Upon the program start, or after sending a request to the underyling debugger,
	 * information may be returned. This string is sent to this method on a per-line
	 * basis. The line may be the end-communication string.</p>
	 * 
	 * <p>This method generaly notifies events to the associated {@link IDebuggerListener}.</p>
	 * 
	 * @param text
	 * @throws DebugException
	 * @throws IOException
	 */
	void interpret(String text) throws DebugException, IOException;
	
	/**
	 * <p>Upon the program start, or after sending a request to the underyling debugger,
	 * information may be returned in the error stream. This string is sent to this method on a per-line
	 * basis. The line may be the end-communication string.</p>
	 * 
	 * <p>This method generaly notifies events to the associated {@link IDebuggerListener}.</p>
	 * 
	 * @param text
	 * @throws DebugException
	 * @throws IOException
	 */
	void interpretError(String text) throws DebugException, IOException;
	
	/**
	 * Starts the execution of the debugge. Any pre-initialization is also
	 * done here.
	 * @throws DebugException
	 * @throws IOException
	 */
	void start() throws DebugException, IOException;
	
	/**
	 * Resumes the execution of a suspended program.
	 * @throws DebugException
	 * @throws IOException
	 */
	void resume() throws DebugException, IOException;
	
	/**
	 * Terminates the execution of a program.
	 * @throws DebugException
	 * @throws IOException
	 */
	void terminate() throws DebugException, IOException;
	
	/**
	 * Adds a breakpoint in the given resource at the given line. 
	 * @param filename the filename in which to add the breakpoint
	 * @param lineNumber the line number in the resource
	 * @throws DebugException
	 * @throws IOException
	 */
	void addBreakpoint(String filename, int lineNumber) throws DebugException, IOException;
	
	/**
	 * Removes a breakpoint from the given resource at the given line. 
	 * @param filename the filename from which to remove the breakpoint
	 * @param lineNumber the line number in the resource
	 * @throws DebugException
	 * @throws IOException
	 */
	void removeBreakpoint(String filename, int lineNumber) throws DebugException, IOException;
	
	/**
	 * Returns the current stack frames.
	 * @return the current stack frames
	 * @throws DebugException
	 * @throws IOException
	 * 
	 * @see IDebugElementFactory
	 */
	IStackFrame[] getStackFrames() throws DebugException, IOException;
	
	/**
	 * Returns the available registers.
	 * @param registerGroup a register group to contain the registers
	 * @return the available registers
	 * @throws IOException
	 * 
	 * @see IDebugElementFactory
	 */
	IRegister[] getRegisters(IRegisterGroup registerGroup) throws IOException;
	
	/**
	 * Returns the local variables and arguments of the given stack frame.
	 * @param stackFrameNumber the number of stack frame
	 * @return the local variables and arguments of the given stack frame
	 * @throws IOException
	 * 
	 * @see IDebugElementFactory
	 */
	IVariable[] getVariables(int stackFrameNumber) throws IOException;
	
	/**
	 * Returns the bytes present at the given <i>startAddress</i> for <i>length</i> bytes.
	 * @param startAddress the start address
	 * @param length the length to return
	 * @return the bytes
	 * @throws IOException
	 */
	byte[] getMemoryBlock(long startAddress, long length) throws IOException;
	
	/**
	 * Steps over.
	 * @throws IOException
	 */
	void stepOver() throws IOException;
	
	/**
	 * Steps into.
	 * @throws IOException
	 */
	void stepInto() throws IOException;
	
	/**
	 * Steps return.
	 * @throws IOException
	 */
	void stepReturn() throws IOException;

	/**
	 * Evaluates an expression in the given stack frame.
	 * @param stackFrameNumber the number of stack frame
	 * @param expression the expression to evaluate
	 * @return the result of the evaluation. May return <code>null</code> if an
	 * error was found while evaluating
	 * @throws IOException
	 * 
	 * @see {@link IDebugElementFactory}
	 */
	IVariable evaluateExpression(int stackFrameNumber, String expression) throws IOException;

}
