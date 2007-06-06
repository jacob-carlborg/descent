/**
 * 
 */
package dtool.dom.base;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TypeDArray;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;

public class TypeDynArray extends Entity {
	public BaseEntityRef.TypeConstraint elemtype;

	public TypeDynArray(TypeDArray elem) {
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

	public String toString() {
		return elemtype + "[]";
	}

	@Override
	public DefUnit getTargetDefUnit() {
		// TODO: return INTRISINC
		return null;
	}
}