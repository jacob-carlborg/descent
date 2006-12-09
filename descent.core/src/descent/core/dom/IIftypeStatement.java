package descent.core.dom;

import descent.internal.core.dom.IftypeDeclaration;
import descent.internal.core.dom.SimpleName;
import descent.internal.core.dom.Type;

public interface IIftypeStatement extends IConditionalStatement {
	/**
	 * Returns whether the condition is:
	 * <ul>
	 * <li><code>iftype(x)</code> (IFTYPE_NONE)</li>
	 * <li><code>iftype(x == y)</code> (IFTYPE_EQUALS)</li>
	 * <li><code>iftype(x : y)</code> (IFTYPE_EXTENDS)</li>
	 * 
	 */
	IftypeDeclaration.Kind getKind();
	
	Type getTestType();
	
	Type getMatchingType();
	
	SimpleName getName();

}
