/**
 * 
 */
package dtool.dom.base;

import descent.internal.core.dom.TypeSArray;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DefUnit;

public class TypeStaticArray extends Entity {
	public Entity elemtype;
	public ASTNode sizeExp;

	public TypeStaticArray(TypeSArray elem) {
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
		// TODO return INTRINSIC
		return null;
	}
}