package descent.core.dom;

/**
 * A problem collector collects problems.
 */
public interface IProblemCollector {
	
	/**
	 * Collects a problem.
	 */
	void collectProblem(IProblem problem);

}
