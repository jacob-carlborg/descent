package descent.core.dom;

public interface IIftypeStatement extends IConditionalStatement {
	
	int IFTYPE_NONE = 0;
	int IFTYPE_EQUALS = 1;
	int IFTYPE_EXTENDS = 2;
	
	/**
	 * Returns whether the condition is:
	 * <ul>
	 * <li><code>iftype(x)</code> (IFTYPE_NONE)</li>
	 * <li><code>iftype(x == y)</code> (IFTYPE_EQUALS)</li>
	 * <li><code>iftype(x : y)</code> (IFTYPE_EXTENDS)</li>
	 * 
	 */
	int getIftypeCondition();
	
	IType getTestType();
	
	IType getMatchingType();
	
	ISimpleName getIdentifier();

}
