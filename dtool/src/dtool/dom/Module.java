package dtool.dom;

import dtool.dom.ext.ASTNeoVisitor;


public class Module extends Symbol {
	
	public DeclarationModule md;
	public ASTElement[] members;
	

	public ArcheType getArcheType() {
		return ArcheType.Module;
	}
	
	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, md);
			acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}


}
