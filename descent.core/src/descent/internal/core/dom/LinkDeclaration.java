package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDElement;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.ILinkDeclaration;

public class LinkDeclaration extends Dsymbol implements ILinkDeclaration {

	private int linkage;
	private IDElement[] declDefs;

	public LinkDeclaration(LINK linkage, List<IDElement> declDefs) {
		if (declDefs != null) {
			this.declDefs = declDefs.toArray(new IDElement[declDefs.size()]);
		}
		this.linkage = linkage.getLinkage();
	}
	
	public int getElementType() {
		return LINK_DECLARATION;
	}
	
	public int getLinkage() {
		return linkage;
	}
	
	public IDElement[] getDeclarationDefinitions() {
		if (declDefs == null) return AbstractElement.NO_ELEMENTS;
		return declDefs;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, declDefs);
		}
		visitor.endVisit(this);
	}

}
