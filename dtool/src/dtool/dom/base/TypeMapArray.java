package dtool.dom.base;

import descent.internal.core.dom.TypeAArray;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DefUnit;

public class TypeMapArray extends Entity {
	public Entity keytype;
	public ASTNode valuetype;

	public TypeMapArray(TypeAArray elem) {
		// TODO Auto-generated constructor stub
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			// TODO: accept children
		}
		visitor.endVisit(this);
	}

	@Override
	public DefUnit getTargetDefUnit() {
		// TODO: return INTRISINC
		return null;
	}
}