package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IAlignDeclaration;
import descent.core.dom.IDElement;
import descent.core.dom.IDElementVisitor;

public class AlignDeclaration extends Dsymbol implements IAlignDeclaration {
	
	public IDElement[] declDefs;
	public long n;

	public AlignDeclaration(long n, List<IDElement> a) {
		this.n = n;
		if (a != null) {
			this.declDefs = a.toArray(new IDElement[a.size()]);
		}
	}
	
	public IDElement[] getDeclarationDefinitions() {
		if (declDefs == null) return AbstractElement.NO_ELEMENTS;
		return declDefs;
	}
	
	public long getAlign() {
		return n;
	}
	
	public int getElementType() {
		return ALIGN_DECLARATION;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, declDefs);
		}
		visitor.endVisit(this);
	}

}
