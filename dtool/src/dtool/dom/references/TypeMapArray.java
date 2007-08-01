package dtool.dom.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import descent.internal.core.dom.TypeAArray;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.references.TypeDynArray.IntrinsicDynArray;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.IntrinsicDefUnit;

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

	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicDynArray.instance);
	}
	
	public static class IntrinsicMapArray extends IntrinsicDefUnit {
		public static final IntrinsicMapArray instance = new IntrinsicMapArray();


		@Override
		public IScopeNode getMembersScope() {
			// TODO Auto-generated method stub
			return null;
		}

		public List<IScope> getSuperScopes() {
			// TODO Auto-generated method stub
			return null;
		}

		public Iterator<? extends ASTNode> getMembersIterator() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}