package descent.core.dom;

import descent.internal.core.dom.QualifiedName;
import descent.internal.core.dom.TypeTypeof;

/**
 * A mixin declaration:
 * 
 * <pre>
 * mixin foo.bar!(arg1, arg2, ..., argN) name;
 * </pre>
 */
public interface IMixinDeclaration extends IDeclaration {
	
	/**
	 * Returns the name of the mixin.
	 */
	IName getName();
	
	/**
	 * Returns the qualified name of the mixin.
	 */
	QualifiedName getType();
	
	/**
	 * Returns the typeof type (TODO: what the hell in the world is this?)
	 */
	TypeTypeof getTypeofType();
	
	/**
	 * Returns the template arguments of the mixin.
	 */
	IDescentElement[] getTemplateArguments();

}
