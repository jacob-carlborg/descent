package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDElement;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IProtectionDeclaration;

public class ProtDeclaration extends Dsymbol implements IProtectionDeclaration {
	
	public PROT prot;
	public IDElement[] declDefs;

	public ProtDeclaration(PROT prot, List<IDElement> declDefs) {
		this.prot = prot;
		if (declDefs != null) {
			this.declDefs = declDefs.toArray(new IDElement[declDefs.size()]);
		}
	}
	
	public IDElement[] getDeclarationDefinitions() {
		if (declDefs == null) return AbstractElement.NO_ELEMENTS;
		return declDefs;
	}
	
	public int getElementType() {
		return PROTECTION_DECLARATION;
	}
	
	public int getProtection() {
		return prot.getModifiers();
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, declDefs);
		}
		visitor.endVisit(this);
	}

}
