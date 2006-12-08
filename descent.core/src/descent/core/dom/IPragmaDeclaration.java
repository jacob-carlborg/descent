package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Declaration;
import descent.internal.core.dom.Expression;

/**
 * A pragma declaration:
 * 
 * <pre>
 * pragma(name, arg1, arg2, ..., argN) { }
 * </pre>
 */
public interface IPragmaDeclaration extends IDeclaration {
	
	/**
	 * Returns the name of the pragma.
	 */
	ISimpleName getName();
	
	/**
	 * Returns the arguments of the pragma.
	 */
	List<Expression> arguments();
	
	/**
	 * Returns the declaration definitions contained in this
	 * pragma declaration.
	 */
	List<Declaration> declarations();

}
