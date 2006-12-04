package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IDeclaration;
import descent.core.dom.IProtectionDeclaration;

public class ProtDeclaration extends Declaration implements IProtectionDeclaration {
	
	public PROT prot;
	public IDeclaration[] declDefs;

	public ProtDeclaration(PROT prot, List<Declaration> declDefs) {
		this.prot = prot;
		if (declDefs != null) {
			this.declDefs = declDefs.toArray(new IDeclaration[declDefs.size()]);
		}
	}
	
	public IDeclaration[] getDeclarationDefinitions() {
		if (declDefs == null) return ASTNode.NO_DECLARATIONS;
		return declDefs;
	}
	
	public int getNodeType0() {
		return PROTECTION_DECLARATION;
	}
	
	public int getProtection() {
		return prot.getModifiers();
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, declDefs);
		}
		visitor.endVisit(this);
	}

}
