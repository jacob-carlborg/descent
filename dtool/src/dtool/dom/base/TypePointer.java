/**
 * 
 */
package dtool.dom.base;

import util.tree.TreeVisitor;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DefUnit;

public class TypePointer extends Entity {
	public EntityConstrainedRef.TypeConstraint elemtype;
	
	public TypePointer(descent.internal.core.dom.TypePointer elem) {
		setSourceRange(elem);
		this.elemtype = Entity.convertType(elem.next);
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, elemtype);
		}
		visitor.endVisit(this);
	}

	@Override
	public DefUnit getTargetDefUnit() {
		// TODO return intrinsic
		return null;
	}
}