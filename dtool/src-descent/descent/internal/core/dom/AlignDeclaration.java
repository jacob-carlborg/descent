package descent.internal.core.dom;

import java.util.List;

import util.tree.IElement;
import util.tree.IVisitable;
import util.tree.TreeVisitor;

import descent.core.dom.IDeclaration;
import descent.core.domX.IASTVisitor;
import descent.core.domX.AbstractElement;

public class AlignDeclaration extends Dsymbol implements IDeclaration {
	
	public IDeclaration[] declDefs;
	public long n;

	public AlignDeclaration(long n, List<IDeclaration> a) {
		this.n = n;
		if (a != null) {
			this.declDefs = a.toArray(new IDeclaration[a.size()]);
		}
	}
	
	public IDeclaration[] getDeclarationDefinitions() {
		if (declDefs == null) return AbstractElement.NO_DECLARATIONS;
		return declDefs;
	}
	
	public long getAlign() {
		return n;
	}
	
	public int getElementType() {
		return ElementTypes.ALIGN_DECLARATION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, (IVisitable[])declDefs);
		}
		visitor.endVisit(this);
	}

}
