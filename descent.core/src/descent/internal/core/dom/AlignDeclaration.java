package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IAlignDeclaration;
import descent.core.dom.ElementVisitor;
import descent.core.dom.IDeclaration;

public class AlignDeclaration extends Dsymbol implements IAlignDeclaration {
	
	public IDeclaration[] declDefs;
	public long n;

	public AlignDeclaration(long n, List<IDeclaration> a) {
		this.n = n;
		if (a != null) {
			this.declDefs = a.toArray(new IDeclaration[a.size()]);
		}
	}
	
	public IDeclaration[] getDeclarationDefinitions() {
		if (declDefs == null) return ASTNode.NO_DECLARATIONS;
		return declDefs;
	}
	
	public long getAlign() {
		return n;
	}
	
	public int getElementType() {
		return ALIGN_DECLARATION;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, declDefs);
		}
		visitor.endVisit(this);
	}

}
