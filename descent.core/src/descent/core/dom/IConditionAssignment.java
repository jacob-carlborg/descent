package descent.core.dom;

/**
 * Represents a conditional debug or version assignment.
 */
public interface IConditionAssignment extends IDeclaration {
	
	/**
	 * A constant representing a debug assignment.
	 */
	int CONDITION_DEBUG = 1;
	
	/**
	 * A constant representing a version assignment.
	 */
	int CONDITION_VERSION = 2;
	
	/**
	 * Returns wheter this is a debug or version assignment. Check
	 * the constants defined in this interface.
	 */
	int getConditionAssignmentType();
	
	/**
	 * The value to assign.
	 */
	IName getValue();

}
