package descent.internal.core.dom;

import java.util.List;

import util.tree.TreeVisitor;

import descent.core.dom.IDeclaration;
import descent.core.dom.ILinkDeclaration;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;

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
		return ElementTypes.LINK_DECLARATION;
	}
	
	public int getLinkage() {
		return linkage;
	}
	
	public IDeclaration[] getDeclarationDefinitions() {
		if (declDefs == null) return AbstractElement.NO_DECLARATIONS;
		return declDefs;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, (AbstractElement[])declDefs);
		}
		visitor.endVisit(this);
	}

}
