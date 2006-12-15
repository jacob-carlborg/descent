package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDeclaration;
import descent.core.dom.IProtectionDeclaration;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;

public class ProtDeclaration extends Dsymbol implements IProtectionDeclaration {
	
	public PROT prot;
	public IDeclaration[] declDefs;

	public ProtDeclaration(PROT prot, List<IDeclaration> declDefs) {
		this.prot = prot;
		if (declDefs != null) {
			this.declDefs = declDefs.toArray(new IDeclaration[declDefs.size()]);
		}
	}
	
	public IDeclaration[] getDeclarationDefinitions() {
		if (declDefs == null) return AbstractElement.NO_DECLARATIONS;
		return declDefs;
	}
	
	public int getElementType() {
		return ElementTypes.PROTECTION_DECLARATION;
	}
	
	public int getProtection() {
		return prot.getModifiers();
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, (AbstractElement[])declDefs);
		}
		visitor.endVisit(this);
	}

}
