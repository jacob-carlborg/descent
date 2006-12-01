package descent.core.dom;

/**
 * A deprecated iftype declaration.
 */
public interface IIftypeDeclaration extends IConditionalDeclaration {
	
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
	
	// TODO: comments this three methods
	
	IType getTestType();
	
	IType getMatchingType();
	
	IName getIdentifier();

}
