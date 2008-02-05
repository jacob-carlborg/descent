package descent.unittest;

import descent.core.IInitializer;

/**
 * Represents a unit test to be run by the descent.unittest plugin. Note that
 * the {@link #hashCode()} method of this class is garunteed to be unique only
 * within the scope of a single project, and if two tests have the same ID, they
 * will have the same hash code and evaluate to equal via {@link #equals(Object)}
 * even if they are in separete projects. Thus, for correct behavior ensure all
 * equality comparisons are done within the same project!
 * 
 * TODO -- is there a way to comapre two projects for equality?
 */
public interface ITestSpecification
{
	/**
	 * Gets the test ID, which is garunteed to be unique within the project
	 * of the declaration returned by getDeclaration(). The format of the
	 * identifier is unspecified by this interface, but will not be null.
	 * 
	 * Implementation note: the current descent.unittest implementation uses
	 * flute, and the test IDs used are the same as the Flute test signatures.
	 * 
	 * @return a unique identifier
	 */
	public String getId();
	
	/**
	 * Gets a human-readable test name. May be null.
	 * 
	 * @return a human-readable tst name string, or null if there is none.
	 */
	public String getName();
	
	/**
	 * Gets the {@link IInitializer} declaration representing the D unittest.
	 * 
	 * @return the D unittest declaration
	 */
	public IInitializer getDeclaration();
}
