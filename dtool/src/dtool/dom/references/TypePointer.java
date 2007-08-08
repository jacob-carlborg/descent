/**
 * 
 */
package dtool.dom.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.NativeDefUnit;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class TypePointer extends CommonRefNative {
	
	public static ASTNeoNode convertTypePointer(descent.internal.core.dom.TypePointer elem) {
		if(elem.next instanceof descent.internal.core.dom.TypeFunction) {
			ASTNeoNode node= new TypeFunction((descent.internal.core.dom.TypeFunction)elem.next);
			node.setSourceRange(elem);
			return node;
		}
		else
			return new TypePointer(elem);
	}


	public Reference elemtype;
	
	private TypePointer(descent.internal.core.dom.TypePointer elem) {
		setSourceRange(elem);
		this.elemtype = Reference.convertType(elem.next);
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, elemtype);
		}
		visitor.endVisit(this);
	}

	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicPointer.instance);
	}
	
	@Override
	public String toString() {
		return elemtype + "*";
	}

	
	public static class IntrinsicPointer extends NativeDefUnit {
		public IntrinsicPointer() {
			super("<pointer>");
		}
		
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