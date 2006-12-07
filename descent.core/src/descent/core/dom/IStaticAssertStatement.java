package descent.core.dom;

import descent.internal.core.dom.StaticAssert;

/**
 * A static assert statement:
 * 
 * <pre>
 * static assert(expr, message);
 * </pre>
 */
public interface IStaticAssertStatement extends IStatement {
	
	StaticAssert getStaticAssert();

}
