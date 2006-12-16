package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Modifier;

/**
 * A declaration in the D language.
 */
public interface IDeclaration extends IElement {
	
	List<Modifier> modifiers();

}
