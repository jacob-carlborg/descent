package dtool.dom.base;

import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DefUnit;

// TODO
public class TypeFunction extends Entity {
	// expression , const

	public TypeFunction(descent.internal.core.dom.TypeFunction elem) {
		// TODO Auto-generated constructor stub
	}

	public void accept0(IASTNeoVisitor visitor) {
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