/**
 * 
 */
package dtool.dom.base;

import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DefUnit;

public class TypePointer extends Entity {
	public Entity elemtype;
	
	public TypePointer(descent.internal.core.dom.TypePointer elem) {
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
		// TODO return intrinsic
		return null;
	}
}