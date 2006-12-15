package descent.core.dom;

import descent.core.compiler.IProblem;

/**
 * A problem collector collects problems.
 */
public interface IProblemCollector {
	
	/**
	 * Collects a problem.
	 */
	void collectProblem(IProblem problem);

}
