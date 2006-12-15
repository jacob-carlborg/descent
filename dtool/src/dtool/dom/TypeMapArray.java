package dtool.dom;

import descent.core.domX.ASTNode;
import dtool.dombase.ASTNeoVisitor;

public class TypeMapArray extends Entity {
	public Entity keytype;
	public ASTNode valuetype;

	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			// TODO: accept children
		}
		visitor.endVisit(this);
	}

	@Override
	public DefUnit getReferencedDefUnit() {
		// TODO: return INTRISINC
		return null;
	}
}