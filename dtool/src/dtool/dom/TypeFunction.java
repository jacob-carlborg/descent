package dtool.dom;

import dtool.dombase.ASTNeoVisitor;

// TODO
public class TypeFunction extends Entity {
	// expression , const

	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}

	@Override
	public DefUnit getReferencedDefUnit() {
		// TODO return INTRINSIC
		return null;
	}
}