package descent.internal.core.dom;

import descent.core.dom.IElement;
import descent.core.domX.AbstractElement;

public abstract class Dsymbol extends AbstractElement implements IElement {

	public Identifier ident;
	public Dsymbol parent;

}