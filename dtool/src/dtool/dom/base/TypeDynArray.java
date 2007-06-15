/**
 * 
 */
package dtool.dom.base;

import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TypeDArray;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.model.IScope;
import dtool.model.IntrinsicDefUnit;

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
		return IntrinsicDynArray.instance;
	}
	
	public static class IntrinsicDynArray extends IntrinsicDefUnit {
		public static final IntrinsicDynArray instance = new IntrinsicDynArray();

		public List<DefUnit> getDefUnits() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IScope getMembersScope() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IScope getSuperScope() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}