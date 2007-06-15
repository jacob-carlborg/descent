package dtool.dom.base;

import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TypeAArray;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.TypeDynArray.IntrinsicDynArray;
import dtool.dom.definitions.DefUnit;
import dtool.model.IScope;
import dtool.model.IntrinsicDefUnit;

public class TypeMapArray extends Entity {
	public Entity keytype;
	public Entity valuetype;

	public TypeMapArray(TypeAArray elem) {
		setSourceRange(elem);
		this.valuetype = Entity.convertType(elem.next);
		this.keytype = Entity.convertType(elem.index);
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, keytype);
			TreeVisitor.acceptChildren(visitor, valuetype);
		}
		visitor.endVisit(this);
	}

	@Override
	public DefUnit getTargetDefUnit() {
		return IntrinsicDynArray.instance;
	}
	
	public static class IntrinsicMapArray extends IntrinsicDefUnit {
		public static final IntrinsicMapArray instance = new IntrinsicMapArray();

		public List<DefUnit> getDefUnits() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IScope getMembersScope() {
			// TODO Auto-generated method stub
			return null;
		}

		public List<IScope> getSuperScopes() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}