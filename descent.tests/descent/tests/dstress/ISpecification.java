package descent.tests.dstress;

import descent.internal.compiler.parser.Module;

/**
 * A specification of a test.
 */
public interface ISpecification {
	
	/**
	 * Returns the api level for the specification.
	 * @return the api level for the specification
	 */
	int getApiLevel();

	/**
	 * Validates the module for the given specification. If
	 * it isn't valid, an exception is thrown.
	 * @param module the module to validate
	 */
	void validate(char[] source, Module module) throws Exception;

}
