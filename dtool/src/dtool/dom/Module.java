package dtool.dom;

import descent.core.domX.ASTNode;
import dtool.dom.ext.ASTNeoVisitor;

/**
 * D Module
 */
public class Module extends SymbolDef {
	
	public DeclarationModule md;
	public ASTNode[] members; //FIXME
	

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

	public static class DeclarationModule extends ASTElement {

		public SymbolReference[] packages;
		public SymbolReference moduleName; // XXX: SymbolReference?

		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				acceptChildren(visitor, packages);
				acceptChild(visitor, moduleName);
			}
			visitor.endVisit(this);
		}
	}

}
