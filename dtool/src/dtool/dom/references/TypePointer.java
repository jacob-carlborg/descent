/**
 * 
 */
package dtool.dom.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.NativeDefUnit;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class TypePointer extends CommonRefNative {
	
	public static ASTNeoNode convertTypePointer(descent.internal.compiler.parser.TypePointer elem) {
		if(elem.next instanceof descent.internal.compiler.parser.TypeFunction) {
			ASTNeoNode node= new TypeFunction((descent.internal.compiler.parser.TypeFunction)elem.next);
			node.setSourceRange(elem);
			return node;
		}
		else
			return new TypePointer(elem);
	}


	public Reference elemtype;
	
	private TypePointer(descent.internal.compiler.parser.TypePointer elem) {
		setSourceRange(elem);
		this.elemtype = ReferenceConverter.convertType(elem.next);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, elemtype);
		}
		visitor.endVisit(this);
	}

	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicPointer.instance);
	}
	
	@Override
	public String toStringAsElement() {
		return elemtype.toStringAsElement() + "*";
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