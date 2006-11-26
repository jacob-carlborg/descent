package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IDeclaration;
import descent.core.dom.ILinkDeclaration;

public class LinkDeclaration extends Dsymbol implements ILinkDeclaration {

	private int linkage;
	private IDeclaration[] declDefs;

	public LinkDeclaration(LINK linkage, List<IDeclaration> declDefs) {
		if (declDefs != null) {
			this.declDefs = declDefs.toArray(new IDeclaration[declDefs.size()]);
		}
		this.linkage = linkage.getLinkage();
	}
	
	public int getElementType() {
		return LINK_DECLARATION;
	}
	
	public int getLinkage() {
		return linkage;
	}
	
	public IDeclaration[] getDeclarationDefinitions() {
		if (declDefs == null) return AbstractElement.NO_DECLARATIONS;
		return declDefs;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, declDefs);
		}
		visitor.endVisit(this);
	}

}
