package descent.launching.environments;


/**
 * Manager for execution environments. The singleton manager is available
 * via <code>JavaRuntime.getExecutionEnvironmentsManager()</code>.
 * <p>
 * Clients are not intended to implement this interface.
 * </p>
 * @since 3.2
 */
public interface IExecutionEnvironmentsManager {

	/**
	 * Returns all registered execution environments.
	 * 
	 * @return all registered execution environments
	 */
	public IExecutionEnvironment[] getExecutionEnvironments();
	
	/**
	 * Returns the execution environment associated with the given
	 * identifier or <code>null</code> if none.
	 * 
	 * @param id execution environment identifier 
	 * @return execution environment or <code>null</code>
	 */
	public IExecutionEnvironment getEnvironment(String id);
		
}
