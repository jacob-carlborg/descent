/**
 * 
 */
package dtool.dom.base;

import java.util.List;

import util.tree.TreeVisitor;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.TypeDynArray.IntrinsicDynArray;
import dtool.dom.definitions.DefUnit;
import dtool.model.IntrinsicDefUnit;

public class TypePointer extends Entity {
	public BaseEntityRef.TypeConstraint elemtype;
	
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
	protected DefUnit getTargetDefUnitAsRoot() {
		return IntrinsicDynArray.instance;
	}
	
	public static class IntrinsicPointer extends IntrinsicDefUnit {
		public static final IntrinsicPointer instance = new IntrinsicPointer();

		public List<DefUnit> getDefUnits() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}