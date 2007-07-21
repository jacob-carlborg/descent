/**
 * 
 */
package dtool.dom.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.refmodel.EntitySearch;
import dtool.refmodel.IScope;
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

	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return EntitySearch.wrapResult(IntrinsicPointer.instance);
	}

	
	public static class IntrinsicPointer extends IntrinsicDefUnit {
		public static final IntrinsicPointer instance = new IntrinsicPointer();


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