package descent.core;

/**
 * An evaluation result corresponding to a struct literal.
 * 
 * @see IEvaluationResult
 * @see ICodeAssist#codeEvaluate(int)
 */
public interface IStructLiteral {
	
	/**
	 * Returns the name of the struct that this struct
	 * literal applies for.
	 * @return the name of the struct that this struct
	 * literal applies for
	 */
	String getName();
	
	/**
	 * Returns the names of this struct literal.
	 * @return the names of this struct literal
	 */
	String[] getNames();
	
	/**
	 * Returns the values of this struct literal.
	 * @return the values of this struct literal
	 */
	IEvaluationResult[] getValues(); 

}
