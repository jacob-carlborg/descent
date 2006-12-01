package descent.internal.core.dom;

import descent.core.dom.IElement;

public abstract class Dsymbol extends ASTNode implements IElement {

	public Identifier ident;
	public Dsymbol parent;

}