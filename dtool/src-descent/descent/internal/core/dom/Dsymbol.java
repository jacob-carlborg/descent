package descent.internal.core.dom;

import descent.core.dom.IDescentElement;
import descent.core.domX.AbstractElement;

public abstract class Dsymbol extends AbstractElement implements IDescentElement {

	public Identifier ident;
	public Dsymbol parent;
	
	public void XPTO() {}

	
/*	public String toString() {
		return ((ident != null) ? ident.string : "<null>")
	}
	*/
}