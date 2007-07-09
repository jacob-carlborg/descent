/**
 * 
 */
package dtool.dom.references;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.references.TypeDynArray.IntrinsicDynArray;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.IntrinsicDefUnit;

public class TypePointer extends Entity {
	public Entity elemtype;
	
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
		return IntrinsicDynArray.instance;
	}
	
	public static class IntrinsicPointer extends IntrinsicDefUnit {
		public static final IntrinsicPointer instance = new IntrinsicPointer();

		public List<DefUnit> getDefUnits() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IScopeNode getMembersScope() {
			// TODO Auto-generated method stub
			return null;
		}

		public List<IScopeNode> getSuperScopes() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}