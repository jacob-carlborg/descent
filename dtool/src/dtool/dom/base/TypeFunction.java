package dtool.dom.base;

import dtool.dom.ast.ASTNeoVisitor;

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
	public DefUnit getTargetDefUnit() {
		// TODO return INTRINSIC
		return null;
	}
}